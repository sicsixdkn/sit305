package com.sicsix.itube.utility

import java.net.URI
import java.security.MessageDigest

class Utility {
    companion object {
        /**
         * Extracts the video ID from a YouTube URL
         *
         * @param url The URL to extract the video ID from
         * @return The video ID or null if the URL is invalid
         */
        fun extractVideoId(url: String): String? {
            val uri = URI(url)
            val query = uri.query
            // Split the query string into key-value pairs
            val queryPairs = query.split("&").associate {
                val index = it.indexOf("=")
                if (index > -1) {
                    it.substring(0, index) to it.substring(index + 1)
                } else it to ""
            }
            // Return the value of the "v" key, this will be the video id if it exists
            return queryPairs["v"]
        }

        /**
         * Validates an entry to ensure it is not empty
         *
         * @param value The value to validate
         * @return An error message if the value is empty, null otherwise
         */
        fun validateEntry(value: String): String? {
            return if (value.isBlank()) "Cannot be empty" else null
        }

        /**
         * Hashes a password using SHA-256
         *
         * @param password The password to hash
         * @return The hashed password
         */
        fun hashPassword(password: String): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}