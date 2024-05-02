package com.example.musorab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musorab.databinding.ActivityMain2Binding
import com.example.musorab.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    lateinit var binding1: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1 = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding1.root)
        val button: Button = findViewById<Button>(R.id.bconnect)

    }
    fun OnClick1(view:View){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}