package com.example.green

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.green.CalculatorActivity
import com.example.green.R

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        db = dbHelper.readableDatabase

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString()

            if (validateLogin(login, password)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CalculatorActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid Login or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateLogin(login: String, password: String): Boolean {
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_USERS}
            WHERE ${DatabaseHelper.COLUMN_USER_LOGIN} = ? AND ${DatabaseHelper.COLUMN_USER_PASSWORD} = ?
        """
        val cursor = db.rawQuery(query, arrayOf(login, password))

        val isValid = cursor.count > 0
        cursor.close()

        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
