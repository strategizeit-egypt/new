package com.simcoder.uber

import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException


open class CustomException(override val message: String?, val kind: Kind) : RuntimeException(message) {

    companion object {
        val gson = Gson()

        fun httpErrorWithObject(response: Response<*>?): CustomException {
            var errorMessage: String? = null
            var responseString = ""
            var statusCode = 0
            if (response != null) {
                statusCode = response.code()
            }
            try {
                if (response?.errorBody() != null) {
                    responseString = response.errorBody()!!.string()
                }

                if (responseString.contains("[")) {
                    val apiErrors = gson.fromJson<ApiErrors>(
                        responseString,
                        ApiErrors::class.java
                    )
                    if (apiErrors != null) {
                        errorMessage = if (apiErrors.errors != null) {
                            apiErrors.errors.values.first()[0]
                        } else {
                            apiErrors.message
                        }
                    }

                } else {
                    val apiError = gson.fromJson<ApiError>(
                        responseString,
                        ApiError::class.java
                    )
                    if (apiError != null) {
                        errorMessage = apiError.error ?: apiError.message
                    }
                }

            } catch (e: Exception) {
                if (response != null)
                    errorMessage = statusCode.toString() + " " + response.message()
                e.printStackTrace()
            }

            return CustomException(errorMessage, kind = Kind.HTTP)
        }

        fun http400(response: Response<*>?): CustomException {
            var errorMessage: String? = null
            var responseString = ""
            var statusCode = 0
            if (response != null) {
                statusCode = response.code()
            }
            try {
                if (response?.errorBody() != null) {
                    responseString = response.errorBody()!!.string()
                }

                if (responseString.contains("[")) {
                    val apiErrors = gson.fromJson<ApiErrors>(
                        responseString,
                        ApiErrors::class.java
                    )
                    if (apiErrors != null) {
                        errorMessage = if (apiErrors.errors != null) {
                            apiErrors.errors.values.first()[0]
                        } else {
                            apiErrors.message
                        }
                    }

                } else {
                    val apiError = gson.fromJson<ApiError>(
                        responseString,
                        ApiError::class.java
                    )
                    if (apiError != null) {
                        errorMessage = apiError.error ?: apiError.message
                    }
                }

            } catch (e: Exception) {
                if (response != null)
                    errorMessage = statusCode.toString() + " " + response.message()
                e.printStackTrace()
            }

            return CustomException(errorMessage, kind = Kind.HTTP)
        }


        fun userNotVerified(response: Response<*>): CustomException {
            val message = response.code().toString() + " " + response.message()
            return CustomException(message = message, kind = Kind.USER_NOT_VERIFIED)
        }

        fun notAuthorized(response: Response<*>): CustomException {

            return CustomException("", Kind.NOT_AUTHORIZED)
        }

        fun httpError(response: Response<*>): CustomException {
            return CustomException(response.errorBody().toString(), Kind.HTTP)
        }

        fun networkError(exception: IOException): CustomException {
            return CustomException(exception.message?:"Connection Issue", Kind.NETWORK)
        }

        fun unexpectedError(exception: Throwable): CustomException {
            return CustomException(exception.message, Kind.UNEXPECTED)
        }
        fun notFoundError(response: Response<*>): CustomException {
            return CustomException("", Kind.NOT_FOUND)
        }

        fun notCompletedAccount(response: Response<*>): CustomException {

            return CustomException("", Kind.NOT_COMPLETED)
        }
        fun paymentError(): CustomException {
            return CustomException("", Kind.PAYMENT)
        }
    }


    enum class Kind {
        NETWORK,
        HTTP,
        USER_NOT_VERIFIED,
        NOT_AUTHORIZED,
        NOT_COMPLETED,
        UNEXPECTED ,
        LOCALE_ERROR,
        NOT_FOUND,
        PAYMENT
    }
}