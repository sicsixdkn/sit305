package com.sicsix.lostandfound.utility

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
         * Converts a date to a string
         *
         * @param date The date to convert
         * @return The date as a string
         */
        fun convertDateToString(date: Date): String {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return format.format(date)
        }

        /**
         * Convert a date to a relative time string
         *
         * @param date The date to convert
         * @return The relative time string
         **/
        fun convertDateToRelativeTime(date: Date): String {
            val diff = Date().time - date.time
            return when (val daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)) {
                0L -> "Today"
                1L -> "Yesterday"
                else -> "$daysDiff days ago"
            }
        }
    }
}