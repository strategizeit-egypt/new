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
import com.google.firebase.auth.AuthResult
import com.simcoder.uber.Login.AuthenticationActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.HashMap

/**
 * Fragment Responsible for registering a new user
 */
class DetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var mName: EditText? = null
    private var mRadioGroup: LinearLayout? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initializeObjects()
    }

    /**
     * Register the user, but before that check if every field is correct.
     * After that registers the user and creates an entry for it oin the database
     */
    private fun register() {
        if (mName!!.text.length == 0) {
            mName!!.error = "please fill this field"
            return
        }
        val name = mName!!.text.toString()
        val accountType: String
        val selectId = 1 //mRadioGroup.getPosition();
        accountType = if (selectId == 1) {
            "Drivers"
        } else {
            "Customers"
        }
        val user_id = FirebaseAuth.getInstance().currentUser!!.uid
        val newUserMap: MutableMap<String, Any> = HashMap()
        newUserMap["name"] = name
        newUserMap["profileImageUrl"] = "default"
        if (accountType == "Drivers") {
            newUserMap["service"] = "type_1"
            newUserMap["activated"] = true
        }
        FirebaseDatabase.getInstance().reference.child("Users").child(accountType).child(user_id)
            .updateChildren(newUserMap).addOnCompleteListener(
            OnCompleteListener { task: Task<Void?>? ->
                val intent = Intent(this@DetailsActivity, LauncherActivity::class.java)
                startActivity(intent)
                finish()
            }
        )
    }

    /**
     * Initializes the design Elements and calls clickListeners for them
     */
    private fun initializeObjects() {
        mName = findViewById(R.id.name)
        val mRegister = findViewById<Button>(R.id.register)
        mRadioGroup = findViewById(R.id.radioRealButtonGroup)

        //mRadioGroup.setPosition(0, false);
        mRegister.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.register) {
            register()
        }
    }
}