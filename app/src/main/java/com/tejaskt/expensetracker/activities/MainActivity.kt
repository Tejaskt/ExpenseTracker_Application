package com.tejaskt.expensetracker.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tejaskt.expensetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var mSignupHere: TextView

    private lateinit var mDialog: ProgressDialog

    //Firebase..
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }

        mDialog = ProgressDialog(this)

        loginDetails()
    }

    private fun loginDetails() {
        mEmail = binding.emailLogin
        mPass = binding.passwordLogin
        btnLogin = binding.btnLogin
        mSignupHere = binding.signupReg

        btnLogin.setOnClickListener(View.OnClickListener {

            val email = mEmail.text.toString().trim { it <= ' ' }
            val pass = mPass.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email)) {
                mEmail.error = "Email Required.."
                return@OnClickListener
            }
            if (TextUtils.isEmpty(pass)) {
                mPass.error = "Password Required.."
                return@OnClickListener
            }

            mDialog.setMessage("Processing..")
            mDialog.show()
            mDialog.setCancelable(false)

            mAuth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mDialog.dismiss()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "Login Successful..", Toast.LENGTH_SHORT).show()
                    } else {
                        mDialog.dismiss()
                        Toast.makeText(this, "Login Failed..", Toast.LENGTH_SHORT).show()

                    }
                }
        })

        // Registration Activity
        mSignupHere.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
            finish()
        }

    }
}