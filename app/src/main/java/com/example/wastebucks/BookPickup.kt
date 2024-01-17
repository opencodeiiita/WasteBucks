package com.example.wastebucks

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.travijuu.numberpicker.library.NumberPicker

class BookPickup : Fragment() {

    private lateinit var db: FirebaseFirestore

    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_book_pickup, container, false)


        db = FirebaseFirestore.getInstance()

        val plasticAmountPicker: NumberPicker = view.findViewById(R.id.numberPickerPlastic)
        val cardboardAmountPicker: NumberPicker = view.findViewById(R.id.numberPickerCardboard)
        val metalAmountPicker: NumberPicker = view.findViewById(R.id.numberPickerMetal)

        plasticAmountPicker.setMax(10)
        plasticAmountPicker.setMin(0)
        plasticAmountPicker.setUnit(1)
        plasticAmountPicker.setValue(0)

        cardboardAmountPicker.setMax(10)
        cardboardAmountPicker.setMin(0)
        cardboardAmountPicker.setUnit(1)
        cardboardAmountPicker.setValue(0)

        metalAmountPicker.setMax(10)
        metalAmountPicker.setMin(0)
        metalAmountPicker.setUnit(1)
        metalAmountPicker.setValue(0)

        val address: EditText = view.findViewById(R.id.editTextAddress)

        val bookButton: Button = view.findViewById(R.id.buttonBookPickup)
        // upload all the data to firebase collection orders

        bookButton.setOnClickListener {
            val order = hashMapOf(
                "plasticAmount" to plasticAmountPicker.getValue(),
                "cardboardAmount" to cardboardAmountPicker.getValue(),
                "metalAmount" to metalAmountPicker.getValue(),
                "address" to address.text.toString(),
                "timestamp" to System.currentTimeMillis(),
                "userId" to FirebaseAuth.getInstance().currentUser?.uid,
            )

            db.collection("orders")
                .add(order)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(context, "Pickup successfully booked! Your pickupId is: ${documentReference.id}", Toast.LENGTH_LONG).show()
                    // Create the Dialog here
                    dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.custom_dialog_layout)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_background))
                    }
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.setCancelable(false) // Optional
                    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Setting the animations to dialog

                    val okayButton: Button = dialog.findViewById(R.id.btn_okay)

                    okayButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    val fragment = Home()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                .addOnFailureListener { e ->
                    println("Error adding document: $e")
                }
        }
        return view
    }
}