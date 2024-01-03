package com.example.wastebucks

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wastebucks.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreatePost : AppCompatActivity() {

    private lateinit var editTextPost: EditText
    private lateinit var imageViewSelected: ImageView
    private var imageUri: Uri? = null
    val PICK_IMAGE_REQUEST = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        editTextPost = findViewById(R.id.editTextPost)
        imageViewSelected = findViewById(R.id.imageViewSelected)

        val buttonUploadImage: Button = findViewById(R.id.buttonUploadImage)
        buttonUploadImage.setOnClickListener {
            selectImage()
        }

        val buttonPost: Button = findViewById(R.id.buttonPost)
        buttonPost.setOnClickListener {
            uploadPost()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadPost() {
        val postText = editTextPost.text.toString()

        if (postText.isEmpty()) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser

        val postMap = hashMapOf(
            "content" to postText,
            "userId" to currentUser?.uid.toString(),
            "likes" to 0,
        )

        if (imageUri != null) {
            val imageName = "images/" + System.currentTimeMillis() + ".jpg"
            val imageRef = storageRef.child(imageName)

            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        postMap["contentImageURL"] = uri.toString()
                        db.collection("posts").add(postMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT)
                                    .show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add post", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            db.collection("posts").add(postMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add post", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imageViewSelected.setImageBitmap(bitmap)
                imageViewSelected.visibility = ImageView.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
