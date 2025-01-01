package com.example.green

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.green.LoginActivity
import com.example.green.CalculatorActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.green.ui.theme.GreenTheme
import com.example.green.R
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logo = findViewById<ImageView>(R.id.logo)
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000
            fillAfter = true
        }
        logo.startAnimation(fadeIn)


        val loginButton: Button = findViewById(R.id.loginButton)
        val calculateButton: Button = findViewById(R.id.calculateButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        calculateButton.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }


        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
