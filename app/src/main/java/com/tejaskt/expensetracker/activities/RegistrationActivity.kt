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
import com.tejaskt.expensetracker.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private lateinit var mEmail: EditText
    private lateinit var mPass: EditText
    private lateinit var btnReg: Button
    private lateinit var mSignin: TextView
    private lateinit var mDialog: ProgressDialog

    // Firebase...
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialog = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance()

        registration()
    }

    private fun registration() {
        mEmail = binding.emailReg
        mPass = binding.passwordReg
        btnReg = binding.btnReg
        mSignin = binding.signinHere

        btnReg.setOnClickListener(View.OnClickListener {

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

            mDialog.setMessage("Processing...")
            mDialog.show()
            mDialog.setCancelable(false)

            mAuth?.createUserWithEmailAndPassword(email, pass)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mDialog.dismiss()
                    Toast.makeText(this, "Registration Complete", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    mDialog.dismiss()
                    Toast.makeText(this, "Registration Fail", Toast.LENGTH_SHORT).show()
                }
            }
        })

        mSignin.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    MainActivity::class.java
                )
            )
            finish()
        }

    }

}