package com.simcoder.uber.Login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.simcoder.uber.R

/**
 * First activity of the app.
 *
 *
 * Responsible for checking if the user is logged in or not and call
 * the AuthenticationActivity or MainActivity depending on that.
 */
class LauncherActivity : AppCompatActivity() {
    var triedTypes = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //   FirebaseAuth mAuth = FirebaseAuth.getInstance();
        /*   if (mAuth.getCurrentUser() != null) {
            checkUserAccType();
        } else {*/Handler().postDelayed({


            //    if (! AppSharedRepository.hasUserLogin(getApplicationContext())){
            val intent = Intent(this@LauncherActivity, AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 2000)

        //   }
    }

    /**
     * Check user account type, either customer or driver.
     * If it doesn't have a type then start the DetailsActivity for the
     * user to be able to pick one.
     */
/*    private fun checkUserAccType() {
        val userID: String
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        val mCustomerDatabase =
            FirebaseDatabase.getInstance().reference.child("Users").child("Customers").child(userID)
        mCustomerDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                  //  startApis("Customers")
                    val intent = Intent(application, CustomerMapActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    checkNoType()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val mDriverDatabase =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(userID)
        mDriverDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                  //  startApis("Drivers")
                    val intent = Intent(application, DriverMapActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    checkNoType()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /**
     * checks if both types have not been fetched meaning the DetailsActivity must be called
     */
    fun checkNoType() {
        triedTypes++
        if (triedTypes == 2) {
            val intent = Intent(application, DetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * starts up onesignal and stripe apis
     * @param type - type of the user (customer, driver)
     */
  /*  fun startApis(type: String?) {
        OneSignal.startInit(this).init()
        OneSignal.sendTag("User_ID", FirebaseAuth.getInstance().currentUser!!.uid)
        OneSignal.setEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
        //OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        OneSignal.idsAvailable { userId: String?, registrationId: String? ->
            FirebaseDatabase.getInstance().reference.child("Users").child(
                type!!
            ).child(FirebaseAuth.getInstance().currentUser!!.uid).child("notificationKey")
                .setValue(userId)
        }
        PaymentConfiguration.init(
            applicationContext,
            resources.getString(R.string.publishablekey)
        )
    }*/
}