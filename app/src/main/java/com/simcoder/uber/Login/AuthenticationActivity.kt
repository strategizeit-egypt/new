package com.simcoder.uber.Login

import androidx.appcompat.app.AppCompatActivity
import com.simcoder.uber.Login.MenuFragment
import android.os.Bundle
import com.simcoder.uber.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import com.simcoder.uber.Login.RegisterFragment
import android.widget.EditText
import android.widget.LinearLayout
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import android.view.ViewGroup
import com.simcoder.uber.Login.DetailsActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.simcoder.uber.Login.AuthenticationActivity
import com.google.android.gms.common.ConnectionResult

/**
 * This Activity controls the display of auth fragments of the app:
 * -MenuFragment
 * -LoginFragment
 * -RegisterFragment
 *
 *
 * It is also responsible for taking the user to the main activity if the login or register process is successful
 */
class AuthenticationActivity : AppCompatActivity() {
    var fm = supportFragmentManager
    var menuFragment = MenuFragment()
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)


        //Listens for changes in the auth state
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth? ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@AuthenticationActivity, LauncherActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        fm.beginTransaction()
            .replace(R.id.container, menuFragment, "StartFragment")
            .addToBackStack(null)
            .commit()
    }

    /**
     * Displays the RegisterFragment
     */
    fun registrationClick() {
        fm.beginTransaction()
            .replace(R.id.container, RegisterFragment(), "RegisterFragment")
            .addToBackStack(null)
            .commit()
    }

    /**
     * Displays the LoginFragment
     */
    fun loginClick() {
        fm.beginTransaction()
            .replace(R.id.container, LoginFragment(), "RegisterFragment")
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        //    FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthListener);
    }

    override fun onStop() {
        super.onStop()
        //   FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthListener);
    }
}