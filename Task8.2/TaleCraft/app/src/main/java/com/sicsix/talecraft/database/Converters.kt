package com.sicsix.talecraft.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Converters for Room database
class Converters {
    /**
     * Converts a string to a list of strings.
     *
     * @param value The string to convert.
     * @return The list of strings.
     */
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    /**
     * Converts a list of strings to a string.
     *
     * @param list The list of strings to convert.
     * @return The string.
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}