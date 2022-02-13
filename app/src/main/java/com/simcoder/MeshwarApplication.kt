package com.simcoder

import android.app.Application


class MeshwarApplication :  Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

       // if (AppSharedRepository.isLogin())
         //   portalChangedEvent.value = AppSharedRepository.getSelectedCommunity()



  /*      if (BuildConfig.BUILD_TYPE != "development")
            Fabric.with(this, Crashlytics())*/

    }


/*
    override fun applicationInjector(): AndroidInjector<MeshwarApplication> =
        DaggerAppComponent.builder().create(this)
*/

/*

    override fun attachBaseContext(base: Context) {
  //     val ctx = base.changeLanguage(AppSharedRepository.getLanguageID(base))
        super.attachBaseContext(ctx)
      //  MultiDex.install(this)
    }
*/
/*

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeLanguage(AppSharedRepository.getLanguageID())
    }
*/

    companion object{
        var instance  : MeshwarApplication? = null
        var isDriver : Boolean = false
      //  val portalChangedEvent  = MutableLiveData<CommunityUI>()
    }


}