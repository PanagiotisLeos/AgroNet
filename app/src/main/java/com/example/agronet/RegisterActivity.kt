package com.example.agronet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        // Εύρεση του Spinner από το layout
        val profileSpinner: Spinner = findViewById(R.id.profileSpinner)

        // Λίστα με τις επιλογές, συμπεριλαμβανομένου του "choose_a_profile"
        val profiles = listOf("Choose a profile...", "Customer", "Farmer")

        // Δημιουργία ενός ArrayAdapter από τη λίστα
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profiles) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                // Ορισμός του χρώματος για κάθε επιλογή
                if (position == 0) {
                    textView.setTextColor(Color.GRAY) // Χρώμα για το "Choose a profile..."
                } else {
                    textView.setTextColor(Color.BLACK) // Μαύρο χρώμα για τις υπόλοιπες επιλογές
                }

                return view
            }
        }

        // Ορισμός του στυλ του dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Σύνδεση του ArrayAdapter με το Spinner
        profileSpinner.adapter = adapter

        // Ορισμός του Listener για το Spinner
        profileSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Εδώ μπορείς να ορίσεις το χρώμα που θέλεις όταν επιλέγεται μια επιλογή από το Spinner
                val selectedColor = Color.WHITE
                (view as? TextView)?.setTextColor(selectedColor)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Τίποτα δεν έχει επιλεγεί, δεν χρειάζεται να γίνει κάτι εδώ
            }
        }

        // Εύρεση του κουμπιού "Sign Up"
        val buttonSignUp: Button = findViewById(R.id.signUpButton)

        // Ορισμός ακροατή για το κουμπί "Sign Up"
        buttonSignUp.setOnClickListener {
            // Μετάβαση στην ProductActivity
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            // Προαιρετικό: Κλείσιμο της τρέχουσας δραστηριότητας
            finish()
        }
    }

    fun farmersClicked(view: View?) {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}
