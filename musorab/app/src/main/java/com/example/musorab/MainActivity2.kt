package com.example.musorab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.musorab.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    lateinit var binding1: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1 = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding1.root)
        val button: Button = findViewById<Button>(R.id.bconnect)

    }
    fun OnClick1(view:View){ // Слушатель нажатий
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}