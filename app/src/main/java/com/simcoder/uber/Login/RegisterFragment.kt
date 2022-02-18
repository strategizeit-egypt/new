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

/**
 * Fragment Responsible for registering a new user
 */
class RegisterFragment : Fragment(), View.OnClickListener {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var currenView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (view == null) currenView = inflater.inflate(
            R.layout.fragment_registration,
            container,
            false
        ) else container!!.removeView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObjects()
    }

    /**
     * Register the user, but before that check if every field is correct.
     * After that registers the user and creates an entry for it oin the database
     */
    private fun register() {
        if (mEmail!!.text.length == 0) {
            mEmail!!.error = "please fill this field"
            return
        }
        if (mPassword!!.text.length == 0) {
            mPassword!!.error = "please fill this field"
            return
        }
        if (mPassword!!.text.length < 6) {
            mPassword!!.error = "password must have at least 6 characters"
            return
        }
        val email = mEmail!!.text.toString()
        val password = mPassword!!.text.toString()
        val intent = Intent(activity, DetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()

/*        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if(!task.isSuccessful()){
                Snackbar.make(view.findViewById(R.id.layout), "sign up error", Snackbar.LENGTH_SHORT).show();
            }
        });*/
    }

    /**
     * Initializes the design Elements and calls clickListeners for them
     */
    private fun initializeObjects() {
        mEmail = currenView!!.findViewById(R.id.email)
        mPassword = currenView!!.findViewById(R.id.password)
        val mRegister = currenView!!.findViewById<Button>(R.id.register)
        mRegister.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.register) {
            register()
        }
    }
}