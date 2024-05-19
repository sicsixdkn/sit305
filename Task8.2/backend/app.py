import json
import os
import re
import requests
from flask import Flask, request, render_template, Response
from flask_restful import Resource, Api
from flask_pymongo import PyMongo
from flask_bcrypt import Bcrypt
from dotenv import load_dotenv
from flask_jwt_extended import JWTManager, jwt_required, create_access_token
from gradientai import Gradient
from datetime import datetime
from abc import ABC, abstractmethod
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


class AbstractGenerator(ABC):
    """
    Abstract class for story generation
    """

    @abstractmethod
    def generate_story_beginning(self, world):
        """ Generate beginning of the story """
        pass

    @abstractmethod
    def generate_next_part(self, world, story, user_selection):
        """ Generate next part of the story """
        pass

    @staticmethod
    def process_generated_text(generated_text):
        """ Process response from Llama into a dictionary """
        story_response = dict()
        story_response["options"] = []

        # Extract story and options from generated text
        pattern = re.compile(r"(?<=STORY:)(.*?)(?=OPTION_A:)|(OPTION_A:.*?)(?=OPTION_B:)|(OPTION_B:.*?)(?=OPTION_C:)|(OPTION_C:.*)",
                             re.DOTALL)
        matches = pattern.findall(generated_text)
        for i, match in enumerate(matches):
            if i == 0:
                story_response["story"] = match[0].strip()
            else:
                option = match[i].strip().split(": ")[1]
                story_response["options"].append(option)

        # Return the processed response
        return story_response


class GradientAIGenerator(AbstractGenerator):
    """
    Class to generate stories using Gradient API
    """
    model_adapter = None

    def __init__(self):
        """ Initialise StoryGenerator """
        gradient = Gradient()
        base_model = gradient.get_base_model(base_model_slug="llama2-7b-chat")
        name = f"Llama_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        self.model_adapter = base_model.create_model_adapter(name=name)

    def generate_story_beginning(self, world):
        """ Generate beginning of the story """
        query = (
            f"GENRE: {world['genre']}\n"
            f"SUBGENRE: {world['subgenre']}\n"
            f"PREMISE: {world['premise']}\n"
            f"[INST] You are an interactive story generator who generates stories for the end user and provides them with options to continue the story.\n"
            f"You are starting a new story.\n"
            f"The user will be a character in the story that you will create.\n"
            f"Generate around 3 paragraphs to begin the story.\n"
            f"Use the GENRE, SUBGENRE, and PREMISE provided above to guide the story generation.\n"
            f"Do not include the USER_SELECTED_OPTION in the generated story text.\n"
            f"Provide the user with 1 to 3 options to choose from to continue the story, where the first option may be just 'Continue...'.\n"
            f"The user options should not be grandiose or major plot points, but rather small, immediate choices that the user can make.\n"
            f"Keep the generated options brief and to the point.\n"
            f"Format your response as follows:\n"
            f"STORY: [generated story text]\n"
            f"OPTION_A: [generated option A]\n"
            f"OPTION_B: [generated option B]\n"
            f"OPTION_C: [generated option C]\n"
            f"[/INST]"
        )
        response = self.model_adapter.complete(query=query, max_generated_token_count=500).generated_output
        return self.process_generated_text(response)

    def generate_next_part(self, world, story, user_selection):
        """ Generate next part of the story """
        query = (
            f"STORY_SO_FAR: {story}\n"
            f"USER_SELECTED_OPTION: {user_selection}\n"
            f"GENRE: {world.genre}\n"
            f"SUBGENRE: {world.subgenre}\n"
            f"PREMISE: {world.premise}\n"
            f"[INST] You are an interactive story generator who generates stories for the end user and provides them with options to continue the story.\n"
            f"You are in the middle of a story.\n"
            f"The user is a character in the story and has made a choice to follow a certain path.\n"
            f"Generate around 1 to 3 more paragraphs of the story from this point using the STORY_SO_FAR and USER_SELECTED_OPTION.\n"
            f"Use the GENRE, SUBGENRE, and PREMISE provided above to guide the story generation.\n"
            f"Do not include the USER_SELECTED_OPTION or the STORY_SO_FAR in the generated story text.\n"
            f"Provide the user with 1 to 3 options to choose from to continue the story, where the first option may be just 'Continue...'.\n"
            f"The user options should not be grandiose or major plot points, but rather small, immediate choices that the user can make.\n"
            f"Keep the generated options brief and to the point.\n"
            f"Format your response as follows:\n"
            f"STORY: [generated story text]\n"
            f"OPTION_A: [generated option A]\n"
            f"OPTION_B: [generated option B]\n"
            f"OPTION_C: [generated option C]\n"
            f"[/INST]"
        )
        response = self.model_adapter.complete(query=query, max_generated_token_count=500).generated_output
        return self.process_generated_text(response)


class LocalAIGenerator(AbstractGenerator):
    """ 
    Class to generate stories using a local Llama 3 8b model with 8 bit quantization (meta-llama-3-8b-instruct-imat-Q8_0.gguf)
    """

    def generate_next_part(self, world, story, user_selection):
        query = (
            f"<|start_header_id|>system<|end_header_id|>"
            f"STORY_SO_FAR: {story}\n"
            f"USER_SELECTED_OPTION: {user_selection}\n"
            f"GENRE: {world['genre']}\n"
            f"SUBGENRE: {world['subgenre']}\n"
            f"PREMISE: {world['premise']}\n"
            f"INSTRUCTIONS:\n"
            f"You are an interactive story generator who generates stories for the end user and provides them with options to continue the story.\n"
            f"You are in the middle of a story.\n"
            f"The user is a character in the story you are creating.\n"
            f"Generate around 1 to 3 more paragraphs of the story from this point using the STORY_SO_FAR and USER_SELECTED_OPTION.\n"
            f"Use the GENRE, SUBGENRE, and PREMISE provided above to guide the story generation.\n"
            f"Do not include the USER_SELECTED_OPTION in the generated story text.\n"
            f"Provide the user with 1 to 3 options to choose from to continue the story, where the first option may be just 'Continue...'.\n"
            f"The user options should not be grandiose or major plot points, but rather small, immediate choices that the user can make.\n"
            f"Keep the generated options brief and to the point. Format your response as follows:\n"
            f"STORY: [generated story text]\n"
            f"OPTION_A: [generated option A]\n"
            f"OPTION_B: [generated option B]\n"
            f"OPTION_C: [generated option C]<|eot_id|>\n"
            f"<|start_header_id|>assistant<|end_header_id|>"
        )
        return self.query_model(query)

    def generate_story_beginning(self, world):
        query = (
            f"<|start_header_id|>system<|end_header_id|>"
            f"GENRE: {world['genre']}\n"
            f"SUBGENRE: {world['subgenre']}\n"
            f"PREMISE: {world['premise']}\n"
            f"INSTRUCTIONS:\n"
            f"You are an interactive story generator who generates stories for the end user and provides them with options to continue the story.\n"
            f"You are starting a new story.\n"
            f"The user will be a character in the story that you will create.\n"
            f"Generate around 3 paragraphs to begin the story.\n"
            f"Use the GENRE, SUBGENRE, and PREMISE provided above to guide the story generation.\n"
            f"Provide the user with 1 to 3 options to choose from to continue the story, where the first option may be just 'Continue...'.\n"
            f"The user options should not be grandiose or major plot points, but rather small, immediate choices that the user can make.\n"
            f"Keep the generated options brief and to the point. Format your response as follows:\n"
            f"STORY: [generated story text]\n"
            f"OPTION_A: [generated option A]\n"
            f"OPTION_B: [generated option B]\n"
            f"OPTION_C: [generated option C]<|eot_id|>\n"
            f"<|start_header_id|>assistant<|end_header_id|>"
        )
        return self.query_model(query)

    @staticmethod
    def query_model(query):
        # query the model using an HTTP POST request to http://localhost:8080/completion
        body = {"prompt": query,
                "frequency_penalty": 0,
                "grammar": "",
                "min_keep": 0,
                "min_p": 0.05,
                "mirostat": 0,
                "mirostat_eta": 0.1,
                "mirostat_tau": 5,
                "n_predict": 400,
                "n_probs": 0,
                "penalize_n1": False,
                "presence_penalty": 0,
                "repeat_last_n": 256,
                "repeat_penalty": 1.18,
                "stream": False,
                "temperature": 0.7,
                "tfs_z": 1,
                "top_k": 40,
                "top_p": 0.95,
                "typical_p": 1}
        headers = {'Content-Type': 'application/json'}
        response = requests.post("http://localhost:8080/completion", data=json.dumps(body), headers=headers)
        return response.json()['content']


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
            'phone_number': data['phone_number']
        })

        # Generate access token and return response
        access_token = create_access_token(identity=str(result.inserted_id), expires_delta=False)
        return {"msg": "User registered successfully", "access_token": access_token,
                "user": {"id": str(result.inserted_id), "username": data['username'], "email": data['email'],
                         "phone_number": data['phone_number']}}, 201


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
            access_token = create_access_token(identity=str(user['_id']), expires_delta=False)
            return {"msg": "Login successful", "access_token": access_token,
                    "user": {"id": str(user['_id']), "username": user['username'], "email": user['email'],
                             "phone_number": user['phone_number']}}, 200
        else:
            return {"msg": "Invalid credentials"}, 401


class Story(Resource):
    """ Story resource to handle story generation and continuation """

    @jwt_required()
    def post(self):
        # Get request data
        data = request.get_json()

        # Check if required fields are present
        if 'story' not in data or 'world' not in data or 'user_selection' not in data:
            return {"msg": "Missing required fields"}, 400

        story = data['story']
        world = data['world']
        user_selection = data['user_selection']

        if 'genre' not in world or 'subgenre' not in world or 'premise' not in world:
            return {"msg": "Missing required fields in world"}, 400

        # Generate story, 10 attempts and if no options are generated, return error
        for i in range(10):
            if story == "":
                generated_text = story_generator.generate_story_beginning(world)
            else:
                generated_text = story_generator.generate_next_part(world, story, user_selection)

            response = story_generator.process_generated_text(generated_text)
            if len(response['options']) > 0:
                break

        if len(response['options']) == 0:
            return {"msg": "Failed to generate story"}, 500

        return response, 200


# Add resources to API
api.add_resource(Register, '/register')
api.add_resource(Login, '/login')
api.add_resource(Story, '/story')

story_generator = LocalAIGenerator()
# Comment out the line above and uncomment the line below to use the Gradient API for story generation
# story_generator = GradientAIGenerator()
