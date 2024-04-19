package com.example.adminfutureapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.adminfutureapp.databinding.ActivitySignUpBinding
import com.example.adminfutureapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {



    private lateinit var email :String
    private lateinit var nameOfRestaurant : String
    private lateinit var password : String
    private lateinit var username : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private val binding : ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // khởi tạo xác thực Firebase
        auth = Firebase.auth
        // khởi tạo xác thực Firebase
        database = Firebase.database.reference



        binding.createUserButton.setOnClickListener {
            // lấy văn bản từ edittext
            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(username.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }

        }
        binding.alreadyHaveAccountButton.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        val locationList = arrayListOf("Ho Chi Minh","Ha Noi","Binh Dinh","Nam Dinh")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Account Create successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Account creation Failed" , Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }

    }
    // lưu dữ liệu vào cơ sở dữ liệu
    private fun saveUserData() {
        username = binding.name.text.toString().trim()
        nameOfRestaurant = binding.restaurantName.text.toString().trim()
        email = binding.emailOrPhone.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user = UserModel(username,nameOfRestaurant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        // lưu dữ liệu người dùng Cơ sở dữ liệu Firebase
        database.child("user").child(userId).setValue(user)

    }
}