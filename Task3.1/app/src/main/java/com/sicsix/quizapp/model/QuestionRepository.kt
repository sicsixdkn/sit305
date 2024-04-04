package com.sicsix.quizapp.model

object QuestionRepository {
    private val questions = listOf(
        Question(
            questionText = "What is Android Jetpack?",
            answer1 = "A set of libraries, tools, and guidelines for Android development",
            answer2 = "A new version of Android",
            answer3 = "A type of Android smartphone",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which file is used to add permissions such as internet access to an Android app?",
            answer1 = "build.gradle",
            answer2 = "AndroidManifest.xml",
            answer3 = "MainActivity.kt",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "In Android Studio, what does the AVD Manager allow you to do?",
            answer1 = "Manage Android Virtual Devices (emulators)",
            answer2 = "Update your Android Studio version",
            answer3 = "Configure your app's permissions",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which of the following is NOT a component of Android Jetpack?",
            answer1 = "ViewModel",
            answer2 = "DataBinding",
            answer3 = "Flutter",
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "What is the primary language recommended for Android development as of the latest Android versions?",
            answer1 = "Java",
            answer2 = "Kotlin",
            answer3 = "C++",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "Which Jetpack library is used for implementing material design components in a Compose application?",
            answer1 = "Compose Material",
            answer2 = "Compose UI",
            answer3 = "Compose Layout",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "What does the term 'lifecycle-aware' mean in the context of Android Jetpack?",
            answer1 = "Components that can automatically adjust their behavior based on the current lifecycle state of an activity or fragment",
            answer2 = "Components that remain active throughout the entire lifecycle of the application",
            answer3 = "Components that destroy themselves once the app's lifecycle ends",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which tool in Android Studio is used to inspect the current layout and view hierarchy of your running app?",
            answer1 = "Android Profiler",
            answer2 = "Logcat",
            answer3 = "Layout Inspector",
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "What is the purpose of the 'Room' library in Android Jetpack?",
            answer1 = "Networking",
            answer2 = "Data persistence",
            answer3 = "Background processing",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "Which of the following is true about Jetpack Compose?",
            answer1 = "It replaces the traditional XML-based layout system",
            answer2 = "It is only used for database management",
            answer3 = "It is a networking library for Android apps",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which of the following is used to manage UI state in a lifecycle-aware manner in Jetpack Compose?",
            answer1 = "LiveData",
            answer2 = "StateFlow",
            answer3 = "Both LiveData and StateFlow",
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "In Android Studio, which tool is used for analyzing the APK to understand its size composition?",
            answer1 = "APK Analyzer",
            answer2 = "Build Analyzer",
            answer3 = "ProGuard",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "What is the purpose of the Hilt library in Android development?",
            answer1 = "Networking",
            answer2 = "Dependency injection",
            answer3 = "Database management",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "Which file extension denotes a Kotlin file?",
            answer1 = ".java",
            answer2 = ".kt",
            answer3 = ".xml",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "In Jetpack Compose, what is the purpose of the Modifier parameter?",
            answer1 = "To modify the appearance of the Android status bar",
            answer2 = "To apply layout and drawing modifications to composables",
            answer3 = "To modify the project's Gradle scripts",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "Which Jetpack library provides backwards compatibility for newer Android features on older devices?",
            answer1 = "Android KTX",
            answer2 = "AppCompat",
            answer3 = "Material Components",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "Which of these is a principle of Jetpack Compose?",
            answer1 = "Imperative UI updates",
            answer2 = "Declarative UI programming",
            answer3 = "XML-based layout design",
            correctAnswerIndex = 2
        ),
        Question(
            questionText = "What does the term 'coroutines' refer to in Kotlin and Android development?",
            answer1 = "A new threading model",
            answer2 = "A type of data structure",
            answer3 = "A library for asynchronous programming and more",
            correctAnswerIndex = 3
        ),
        Question(
            questionText = "In the context of Android app development, what is a 'Fragment'?",
            answer1 = "A part of an activity which enables more modular activity design",
            answer2 = "A small graphical element",
            answer3 = "A deprecated component replaced by Jetpack Compose",
            correctAnswerIndex = 1
        ),
        Question(
            questionText = "Which Android Jetpack component is used to observe data changes and update the UI automatically?",
            answer1 = "DataBinding",
            answer2 = "LiveData",
            answer3 = "ViewModel",
            correctAnswerIndex = 2
        )
    )

    fun getRandomQuestions(count: Int = 5): List<Question> {
        return questions.shuffled().take(count)
    }
}
