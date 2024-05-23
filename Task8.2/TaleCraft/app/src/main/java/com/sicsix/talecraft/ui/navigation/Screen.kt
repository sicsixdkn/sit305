package com.sicsix.talecraft.ui.navigation

// Screen enum class to define the screens in the app
enum class Screen(val route: String, val title: String) {
    Login("login", "Login"),
    SignUp("signup", "Sign Up"),
    Library("library", "Library"),
    Story("story/{storyId}", "Story"),
    Worlds("worlds", "Worlds"),
    CreateStory("createStory/{worldId}", "Create Story"),
    CreateWorld("createWorld", "Create World"),
    Settings("settings", "Settings")
}