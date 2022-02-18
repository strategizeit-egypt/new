package com.simcoder.uber.agora.driver.di

import android.content.Context
import com.google.gson.Gson
import com.simcoder.MeshwarApplication
import com.simcoder.uber.MapsApiServices
import com.simcoder.uber.RxErrorHandlingCallAdapterFactory
import com.simcoder.uber.agora.driver.GpsTracker11
import com.simcoder.uber.agora.driver.data.DriverRepo
import com.simcoder.uber.agora.message.ChatRoom
import com.simcoder.uber.agora.message.data.source.ChatLogin
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

object AppModule {
    val login = getChatLogin()
    fun getChatLogin() = ChatLogin(MeshwarApplication.instance!!)
    fun getChatRoom() = ChatRoom(login,MeshwarApplication.instance!!)
    fun provideDriverRepoImp(): DriverRepo =
        DriverRepo(login,getChatRoom(),  GpsTracker11())
    fun getGps() = GpsTracker11()



    fun provideMapApi()
            : MapsApiServices = getClient().create(
        MapsApiServices::class.java)


    fun getClient(context: Context= MeshwarApplication.instance!!, gson :Gson = Gson()): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        return Retrofit.Builder()

            .client(okHttpClient)
            .baseUrl("https://www.google.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            /* .addCallAdapterFactory(RxJava2CallAdapterFactory.create())*/
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())

            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


}

/*@Module
abstract class DriverHomePresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(DriverHomeViewModel::class)
    internal abstract fun provideDriverHomeViewModel(viewModel: DriverHomeViewModel): ViewModel

}*/


/*@Module
abstract class DriverHomeFragmentModule {

    @ContributesAndroidInjector
        (modules = [DriverHomePresentationModule::class, DriverHomeDataModule::class])
    internal abstract fun driverHomeFragment(): DriverHomeFragment
}*/

