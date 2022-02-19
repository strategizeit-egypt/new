package com.simcoder.uber.Login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.simcoder.uber.Customer.CustomerMapActivity
import com.simcoder.uber.Driver.DriverMap1Activity
import com.simcoder.uber.Driver.DriverMapActivity
import com.simcoder.uber.R

/**
 * Fragment Responsible for Logging in an existing user
 */
class LoginFragment : Fragment(), View.OnClickListener {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var currentView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) currentView =
            inflater.inflate(R.layout.fragment_login, container, false) else container!!.removeView(
            currentView
        )
        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObjects()
    }

    /**
     * Sends an email to the email that's on the email input for the user to reset the password
     */
    private fun forgotPassword() {
        if (mEmail!!.text.toString().trim { it <= ' ' }.length > 0) FirebaseAuth.getInstance()
            .sendPasswordResetEmail(
                mEmail!!.text.toString()
            )
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Snackbar.make(
                        currentView!!.findViewById(R.id.layout),
                        "Email Sent",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else Snackbar.make(
                    currentView!!.findViewById(R.id.layout),
                    "Something went wrong",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Logs in the user
     */
    private fun login() {
        val email = mEmail!!.text.toString()
        val password = mPassword!!.text.toString()
        if (mEmail!!.text.length == 0) {
            mEmail!!.error = "please fill this field"
            return
        }
        if (mPassword!!.text.length == 0) {
            mPassword!!.error = "please fill this field"
            return
        }
        //   AppSharedRepository.setIsDriver(getContext(), false);
        val intent = Intent(activity, DriverMap1Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
/*        if (mPassword!!.text.toString() == "123") {
            //   AppSharedRepository.setUserLogin(getContext(), true);
            //  AppSharedRepository.setIsDriver(getContext(), true);
            val intent = Intent(activity, DriverMap1Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        } else {
            //   AppSharedRepository.setUserLogin(getContext(), true);
            //   AppSharedRepository.setIsDriver(getContext(), false);
            val intent = Intent(activity, CustomerMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }*/

/*        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (!task.isSuccessful()) {
                Snackbar.make(view.findViewById(R.id.layout), "sign in error", Snackbar.LENGTH_SHORT).show();
            }
        });*/
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.forgotButton -> forgotPassword()
            R.id.login -> login()
        }
    }

    /**
     * Initializes the design Elements and calls clickListeners for them
     */
    private fun initializeObjects() {
        mEmail = currentView!!.findViewById(R.id.email)
        mPassword = currentView!!.findViewById(R.id.password)
        val mForgotButton = currentView!!.findViewById<TextView>(R.id.forgotButton)
        val mLogin = currentView!!.findViewById<Button>(R.id.login)
        mForgotButton.setOnClickListener(this)
        mLogin.setOnClickListener(this)
    }
}