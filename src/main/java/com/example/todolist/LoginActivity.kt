package com.example.todolist

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.todolist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccount.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            validateData()
        }


    }
    private var email=""
    private var pass=""

    private fun validateData(){
        email=binding.emailEnter.text.toString().trim()
        pass=binding.passwordEnter.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_SHORT).show()
        }
        else if(pass.isEmpty()){
            Toast.makeText(this,"Enter a password", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser(){
        progressDialog.setMessage("Logging In...")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                checkUser()
                finish()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists=snapshot.exists()
                if (exists){
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }
}