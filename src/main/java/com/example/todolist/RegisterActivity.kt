package com.example.todolist

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.todolist.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.SignAccount.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener{
            validateData()
        }


    }

    private var name=""
    private var email=""
    private var pass=""
    private var cpass=""

    private fun validateData(){
        email=binding.emailEnter.text.toString().trim()
        pass=binding.passwordEnter.text.toString().trim()
        cpass=binding.cPasswordEnter.text.toString().trim()


        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_SHORT).show()
        }
        else if(pass.isEmpty()){
            Toast.makeText(this,"Enter a password", Toast.LENGTH_SHORT).show()
        }
        else if(cpass.isEmpty()){
            Toast.makeText(this,"Confirm the password", Toast.LENGTH_SHORT).show()
        }
        else if(cpass!=pass){
            Toast.makeText(this,"Password doesn't match", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount(){
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                updateUserInfo()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
    private fun updateUserInfo(){
        progressDialog.setMessage("Saving User Info...")
        val timeStamp=System.currentTimeMillis()
        val uid=firebaseAuth.uid
        val new=uid.toString()
        val hashMap:HashMap<String, String> = HashMap()
        hashMap["email"]=email
        hashMap["timeStamp"]= timeStamp.toString()

        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(new)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account Created", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed saving due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}