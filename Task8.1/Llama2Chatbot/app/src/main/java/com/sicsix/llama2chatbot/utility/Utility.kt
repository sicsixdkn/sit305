package com.sicsix.llama2chatbot.utility

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

class Utility {
    companion object {
        /**
         * Validates an entry to ensure it is not empty
         *
         * @param value The value to validate
         * @return An error message if the value is empty, null otherwise
         */
        fun validateEntry(value: String): String? {
            return if (value.isBlank()) "Cannot be empty" else null
        }
    }
}