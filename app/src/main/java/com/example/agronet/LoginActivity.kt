package com.example.agronet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val regButton = findViewById<Button>(R.id.register_button)

        setClearHintOnFocus(emailInput)
        setClearHintOnFocus(passwordInput)

        loginClicked(emailInput)
        regButton.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun loginClicked(view: View){
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
    }

    private fun setClearHintOnFocus(editText: EditText) {
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                editText.hint = ""
            } else {
                // Restore the hint when the EditText loses focus
                if (editText.id == R.id.email_input) {
                    editText.hint = getString(R.string.email_string)
                } else if (editText.id == R.id.password_input) {
                    editText.hint = getString(R.string.password_string)
                }
            }
        }
    }


}
