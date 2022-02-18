package com.simcoder.uber.Login

import androidx.appcompat.app.AppCompatActivity
import com.simcoder.uber.Login.MenuFragment
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
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
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import androidx.fragment.app.Fragment
import com.google.firebase.auth.AuthResult
import com.simcoder.uber.Login.AuthenticationActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.tasks.Task

/**
 * Fragment that allows the user to choose between going to the login or registration fragment
 */
class MenuFragment : Fragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private var mGoogleSignInClient: GoogleSignInClient? = null
    var name: String? = null
    var email: String? = null
    var idToken: String? = null
    private val firebaseAuth: FirebaseAuth? = null
    private var currenView: View? = null
    private var started = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currenView == null) currenView =
            inflater.inflate(R.layout.fragment_menu, container, false) else container!!.removeView(
            currenView
        )


        //   firebaseAuth = FirebaseAuth.getInstance();
        if (!started) {
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        }
        val signInButton: SignInButton = currenView!!.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener { view: View? ->
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        started = true
        return currenView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObjects()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    /**
     * Gets the data out of the result of call.
     * @param result - result of the api call
     */
    private fun handleSignInResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess) {
            val account = result.signInAccount
            idToken = account!!.idToken
            name = account.displayName
            email = account.email
            // you can store user data to SharedPreference
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthWithGoogle(credential)
        } else {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(activity, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * login with the google credential if the account does not exist it will be automatically created
     * @param credential - google auth credential
     */
    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    task.exception!!.printStackTrace()
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.registration -> (activity as AuthenticationActivity?)!!.registrationClick()
            R.id.login -> (activity as AuthenticationActivity?)!!.loginClick()
        }
    }

    /**
     * initializes the design Elements
     */
    private fun initializeObjects() {
        val mLogin = currenView!!.findViewById<Button>(R.id.login)
        val mRegistration = currenView!!.findViewById<Button>(R.id.registration)
        mRegistration.setOnClickListener(this)
        mLogin.setOnClickListener(this)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    companion object {
        private const val RC_SIGN_IN = 1
    }
}