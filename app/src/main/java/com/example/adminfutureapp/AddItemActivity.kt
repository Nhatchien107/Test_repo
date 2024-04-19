package com.example.adminfutureapp

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.adminfutureapp.databinding.ActivityAddItemBinding
import com.example.adminfutureapp.model.AllMenu
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    //chi tiết mặt hàng thực phẩm
    private lateinit var foodName : String
    private lateinit var foodPrice : String
    private lateinit var foodDescription : String
    private lateinit var foodIngredient : String
    private  var foodImageUri: Uri? = null

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase


    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khoi tao Firebase
        auth = FirebaseAuth.getInstance()
        // Khởi tạo phiên bản cơ sở dữ liệu firebase
        database = FirebaseDatabase.getInstance()

        binding.addItemButton.setOnClickListener {
            // Lấy dữ liệu từ form Filed
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.FoodPrice.text.toString().trim()
            foodDescription = binding.derscription.text.toString().trim()
            foodIngredient = binding.ingredint.text.toString().trim()

            if(!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredient.isBlank())){
                uploadData()
                Toast.makeText(this, "Thêm mặt hàng thành công", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Điền vào tất cả các chi tiết", Toast.LENGTH_SHORT).show()
            }

        }
        binding.selectimage.setOnClickListener{
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun uploadData() {
       // lấy tham chiếu đến ghi chú menu trong cơ sở dữ liệu
        val menuRef = database.getReference("menu")
        // Tạo một khóa duy nhất cho menu mới
        val newItemKey = menuRef.push().key

        if(foodImageUri != null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl->
                    // Create a new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodImage = downloadUrl.toString()
                    )
                    newItemKey?.let {
                        key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Dữ liệu được tải lên thành công", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener{
                                Toast.makeText(this, "Dữ liệu được tải lên không thành công", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

            }.addOnFailureListener{
                Toast.makeText(this, "Tải lên hình ảnh không thành công", Toast.LENGTH_SHORT).show()
            }

        }
            else{
                Toast.makeText(this, "Vui lòng chọn một hình ảnh ", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.selectimage.setImageURI(uri)
            foodImageUri = uri

        }
    }
}

