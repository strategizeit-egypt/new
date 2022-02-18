package com.simcoder.uber

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject


class RxErrorHandlingCallAdapterFactory @Inject constructor(

): CallAdapter.Factory() {

    private val _original by lazy {
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    companion object {

        fun create() : CallAdapter.Factory = RxErrorHandlingCallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = _original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
        return RxCallAdapterWrapper(retrofit, wrapped)
    }

    protected class RxCallAdapterWrapper<R>(
        val _retrofit: Retrofit,
        val _wrappedCallAdapter: CallAdapter<R, *>
    ): CallAdapter<R, Any> {

        override fun responseType(): Type = _wrappedCallAdapter.responseType()


        @Suppress("UNCHECKED_CAST")
        override fun adapt(call: Call<R>): Any {
            return when (val result = _wrappedCallAdapter.adapt(call)) {
                is Single<*> -> result.onErrorResumeNext { throwable ->

                    Single.error(asCustomException(throwable, call.request()))
                }

   /*             is Observable<*> -> result.onErrorResumeNext { throwable -> Observable.error<CustomException>(asCustomException(throwable)) }*/

                is Completable -> result.onErrorResumeNext { throwable ->
                    Completable.error(asCustomException(throwable, call.request()))
                }
                else -> result
            }
        }

        private fun asCustomException(throwable: Throwable, request: Request? = null): CustomException {

            // We had non-200 http error
            if (throwable is HttpException) {

                val response = throwable.response()!!

                return when {
                    throwable.code() == 492 ->
                        CustomException.userNotVerified(response)
                    throwable.code() == 401 ->
                        CustomException.notAuthorized(response)
                    throwable.code() == 404 ->
                        CustomException.notFoundError(response)
                    throwable.code() == 422  ->
                        CustomException.httpErrorWithObject(response)
                    throwable.code() == 412 ->
                        CustomException.notCompletedAccount(response)
                    throwable.code() == 409 ->
                        CustomException.httpErrorWithObject(response)
                    throwable.code() == 428 ->
                        CustomException.paymentError()
                    else->
                        CustomException.unexpectedError(throwable)
                }
            }

            // A network error happened
            if (throwable is IOException) {
               /* val email = try { AppSharedRepository.getUser().email }catch (e: Exception){ null }
                email?.let {
                    FireBaseLoggingDS.logErrorNetwork(
                        email = email,
                        ErrorLoggingParam(request.toString(), null, throwable.toString())
                    )
                }*/
                return try {
                    CustomException.networkError(throwable)
                }catch (e: Throwable){
                    CustomException.networkError(IOException("Connection Issue"))
                }
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return CustomException.unexpectedError(throwable)
        }

    }
}