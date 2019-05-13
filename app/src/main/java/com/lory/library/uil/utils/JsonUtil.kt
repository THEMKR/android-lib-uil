package com.lory.library.uil.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class JsonUtil {
    companion object {

        /**
         * Convert the Class Object to the JSON-String
         *
         * @param dataObject the object
         * @param excludeExpose  TRUE : [exclude @Expose]
         * @return the string
         */
        fun toString(dataObject: Any, excludeExpose: Boolean?): String {
            return if (excludeExpose == true) {
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(dataObject)
            } else {
                GsonBuilder().create().toJson(dataObject)
            }

        }

        /**
         * Convert the Class Object to the JSON-String
         *
         * @param dataObject the object
         * @param excludeExpose  TRUE : [exclude @Expose]
         * @return the string
         */
        inline fun <reified MKR> toStringTokenType(dataObject: Any, excludeExpose: Boolean?): String {
            val typeToken = object : TypeToken<MKR>() {}
            return if (excludeExpose == true) {
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(dataObject, typeToken.type)
            } else {
                GsonBuilder().create().toJson(dataObject, typeToken.type)
            }
        }

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================
        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        /**
         * Convert the JSON Object to the Class Object
         *
         * @param jsonString the json string
         * @param excludeExpose  TRUE : [exclude @Expose]
         * @return the object
         */
        inline fun <reified MKR> toObject(jsonString: String, excludeExpose: Boolean?): MKR {
            return if (excludeExpose == true) {
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(jsonString, MKR::class.java)
            } else {
                GsonBuilder().create().fromJson(jsonString, MKR::class.java)
            }
        }

        /**
         * Convert the JSON Object to the Class Object
         *
         * @param jsonString the json string
         * @param excludeExpose  TRUE : [exclude @Expose]
         * @return the object
         */
        inline fun <reified MKR> toObjectTokenType(jsonString: String, excludeExpose: Boolean?): MKR {
            val typeToken = object : TypeToken<MKR>() {}
            return if (excludeExpose == true) {
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(jsonString, typeToken.type)
            } else {
                GsonBuilder().create().fromJson(jsonString, typeToken.type)
            }
        }
    }
}