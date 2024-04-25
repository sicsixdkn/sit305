import os
import re
from flask import Flask, request
from flask_restful import Resource, Api
from flask_pymongo import PyMongo
from flask_bcrypt import Bcrypt
from dotenv import load_dotenv
from flask_jwt_extended import JWTManager, jwt_required, create_access_token, get_jwt_identity
from gradientai import Gradient
from datetime import datetime
from apscheduler.schedulers.background import BackgroundScheduler
from bson.objectid import ObjectId
import random

# Load environment variables
load_dotenv()

# Initialize Flask app
app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = os.getenv("JWT_SECRET_KEY")
app.config["MONGO_URI"] = os.getenv("MONGO_URI")

# Initialize Flask extensions
mongo = PyMongo(app)
bcrypt = Bcrypt(app)
jwt = JWTManager(app)
api = Api(app)


class QuizManager:
    """
    QuizManager class to manage quiz generation and storage
    """

    model_adapter = None

    def __init__(self):
        """ Initialize QuizManager """
        # Initialize Gradient API
        gradient = Gradient()
        base_model = gradient.get_base_model(base_model_slug="llama2-7b-chat")
        name = f"Llama_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        self.model_adapter = base_model.create_model_adapter(name=name)

        # Initialize scheduler to generate quizzes
        scheduler = BackgroundScheduler()
        scheduler.add_job(self.generate_quizzes, 'interval', minutes=1)
        scheduler.start()

    @staticmethod
    def fetch_quiz_from_llama(student_topic, model_adapter):
        """ Fetch quiz from Llama model """
        query = (
            f"[INST] Generate a quiz with 3 questions to test students on the provided topic. "
            f"For each question, generate 4 options where only one of the options is correct. "
            f"Format your response as follows:\n"
            f"QUESTION: [Your question here]?\n"
            f"OPTION A: [First option]\n"
            f"OPTION B: [Second option]\n"
            f"OPTION C: [Third option]\n"
            f"OPTION D: [Fourth option]\n"
            f"ANS: [Correct answer letter]\n\n"
            f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer."
            f"Follow this pattern for all questions."
            f"Here is the student topic:\n{student_topic}"
            f"[/INST]"
        )
        response = model_adapter.complete(query=query, max_generated_token_count=500).generated_output
        return response

    @staticmethod
    def process_quiz(quiz_text):
        """ Process quiz text and return quiz data """
        questions = []
        pattern = re.compile(
            r'QUESTION: (.+?)\n(?:OPTION A: (.+?)\n)+(?:OPTION B: (.+?)\n)+(?:OPTION C: (.+?)\n)+(?:OPTION D: (.+?)\n)+ANS: (.+?)',
            re.DOTALL)
        matches = pattern.findall(quiz_text)

        for match in matches:
            question = match[0].strip()
            options = match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()
            correct_ans = match[-1].strip()

            question_data = {
                "question": question,
                "options": options,
                "correct_answer": correct_ans
            }
            questions.append(question_data)

        return questions

    @staticmethod
    def store_quiz(topic, questions, user_id):
        """ Store quiz in MongoDB """
        quiz = {'user_id': user_id, 'complete': False, 'selected_answers': [], 'topic': topic, 'questions': questions, 'score': 0}
        mongo.db.quizzes.insert_one(quiz)

    def generate_quizzes(self):
        """ Generate quizzes for users """
        users = mongo.db.users.find()
        for user in users:
            # Skip users with no interests
            if not user['interests']:
                continue
            incomplete_quizzes = mongo.db.quizzes.count_documents({"user_id": user['_id'], "complete": False})
            while incomplete_quizzes < 3:
                # Generate quiz for random interest
                topic = random.choice(user['interests'])
                quiz = self.process_quiz(self.fetch_quiz_from_llama(topic, self.model_adapter))
                self.store_quiz(topic, quiz, user['_id'])
                incomplete_quizzes += 1


class Register(Resource):
    """ Register resource to handle user registration """

    @staticmethod
    def post():
        """ Handle POST request to register a new user """

        # Get request data
        data = request.get_json()

        # Check if required fields are present
        if not data.get('username') or not data.get('password') or not data.get('email') or not data.get('phone_number'):
            return {"msg": "Missing required fields"}, 400

        # Check if user already exists
        existing_user = mongo.db.users.find_one({'$or': [{'username': data['username']}, {'email': data['email']}]})
        if existing_user:
            return {"msg": "A user with this username or email already exists"}, 400

        # Hash password and store user in database
        hashed_password = bcrypt.generate_password_hash(data['password']).decode('utf-8')
        result = mongo.db.users.insert_one({
            'username': data['username'],
            'password': hashed_password,
            'email': data['email'],
            'phone_number': data['phone_number'],
            'interests': []
        })

        # Generate access token and return response
        access_token = create_access_token(identity=str(result.inserted_id))
        return {"msg": "User registered successfully", "access_token": access_token,
                "user": {"username": data['username'], "email": data['email'], "phone_number": data['phone_number']}}, 201


class Login(Resource):
    """ Login resource to handle user login """

    @staticmethod
    def post():
        # Get request data
        data = request.get_json()

        # Check if required fields are present
        if not data.get('username') or not data.get('password'):
            return {"msg": "Missing required fields"}, 400

        # Check if user exists and password is correct
        user = mongo.db.users.find_one({'username': data['username']})
        if user and bcrypt.check_password_hash(user['password'], data['password']):
            # Generate access token and return response
            access_token = create_access_token(identity=str(user['_id']))
            return {"msg": "Login successful", "access_token": access_token,
                    "user": {"username": user['username'], "email": user['email'], "phone_number": user['phone_number']}}, 200
        else:
            return {"msg": "Invalid credentials"}, 401


class UserInterests(Resource):
    """ UserInterests resource to handle user interests """

    @jwt_required()
    def put(self):
        """ Handle PUT request to update user interests """
        # Get current user
        current_user = get_jwt_identity()
        user_id = ObjectId(current_user)
        # Get user from database
        user = mongo.db.users.find_one({'_id': user_id})
        # Check if user exists and return error if not
        if user is None:
            return {'msg': 'User not found'}, 404

        # Get request data
        data = request.get_json()

        # Check if 'interests' field is present and is a list, return error if not
        if 'interests' not in data:
            return {'msg': "Missing 'interests' field"}, 400
        interests = data['interests']
        if not isinstance(interests, list):
            return {'msg': "'interests' field must be a list"}, 400

        # Update user interests in database and return response
        mongo.db.users.update_one({'_id': user_id}, {'$set': {'interests': interests}})
        return {'msg': "Interests updated successfully"}, 200


class Interests(Resource):
    """ Interests resource to handle interests """

    @staticmethod
    def get():
        """ Handle GET request to get list of interests """
        interests = [
            "Programming Languages",
            "Algorithms",
            "Software Engineering",
            "Game Development",
            "Computer Networks",
            "Data Science",
            "Operating Systems",
            "Mobile Development",
            "Database Systems",
            "Cloud Computing",
            "Machine Learning",
            "Cybersecurity",
            "Artificial Intelligence",
            "Data Structures",
            "Computer Graphics",
            "Web Development",
            "Testing",
            "Embedded Systems"
        ]
        return {"interests": interests}, 200


class Quizzes(Resource):
    @jwt_required()
    def get(self):
        """ Handle GET request to fetch all quizzes for the logged-in user """
        # Get current user
        current_user = get_jwt_identity()
        user_id = ObjectId(current_user)
        # Fetch quizzes from database
        quizzes = mongo.db.quizzes.find({"user_id": user_id})
        # Convert quizzes to a list (including the quiz id) and return
        quizzes_list = []
        for quiz in quizzes:
            quiz_dict = {**quiz, "quiz_id": str(quiz["_id"]), "user_id": str(quiz["user_id"]), "_id": None}
            quizzes_list.append(quiz_dict)
        return {"quizzes": quizzes_list}, 200

    @jwt_required()
    def put(self):
        """ Handle PUT request to update a quiz with selected answers and mark it as finished """
        # Get current user
        current_user = get_jwt_identity()
        # Get request data
        data = request.get_json()
        # Check if 'quiz_id' and 'selected_answers' fields are present
        if not data.get('quiz_id') or not data.get('selected_answers'):
            return {"msg": "Missing required fields"}, 400

        user_id = ObjectId(current_user)
        quiz_id = ObjectId(data['quiz_id'])
        # Fetch the quiz from the database
        quiz = mongo.db.quizzes.find_one({"_id": quiz_id, "user_id": user_id})
        if not quiz:
            return {"msg": "Quiz not found"}, 404

        # Calculate the score
        score = 0
        for question, selected_answer in zip(quiz['questions'], data['selected_answers']):
            if question['correct_answer'] == selected_answer:
                score += 1

        # Update quiz in database
        mongo.db.quizzes.update_one(
            {"_id": quiz_id, "user_id": user_id},
            {"$set": {"selected_answers": data['selected_answers'], "complete": True, "score": score}}
        )
        return {"msg": "Quiz updated successfully"}, 200


# Add resources to API
api.add_resource(Register, '/register')
api.add_resource(Login, '/login')
api.add_resource(UserInterests, '/userinterests')
api.add_resource(Interests, '/interests')
api.add_resource(Quizzes, '/quizzes')

QuizManager()
