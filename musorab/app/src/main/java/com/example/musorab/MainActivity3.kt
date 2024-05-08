package com.example.musorab

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        }
    fun OnClick2(view: View){ // Слушатель нажатий
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }
}