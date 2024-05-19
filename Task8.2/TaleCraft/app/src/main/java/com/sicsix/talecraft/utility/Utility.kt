package com.sicsix.talecraft.utility

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

        /**
         * Validates a phone number
         *
         * @param phone The phone number to validate
         * @return An error message if the phone number is invalid, null otherwise
         */
        fun validatePhoneNumber(phone: String): String? {
            return try {
                val phoneNumber = PhoneNumberUtil.getInstance().parse(phone, "AU")
                if (!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) "Invalid phone number" else null
            } catch (e: NumberParseException) {
                "Invalid phone number"
            }
        }

        /**
         * Validates an email address
         *
         * @param email The email address to validate
         * @return An error message if the email address is invalid, null otherwise
         */
        fun validateEmail(email: String): String? {
            return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email address" else null
        }
    }
}