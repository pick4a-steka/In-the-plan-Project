package com.example.musorab

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.musorab.databinding.ActivityMain2Binding
import com.example.musorab.databinding.ActivityMainBinding

var A:Int=1
class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("myLog0", "create")
        if (A==1) {
            A=0
            startActivity(Intent(this, MainActivity2::class.java))
                finish()
        }

        // Создание нового экземпляра CameraX
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Настройка превью
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.Camera1.surfaceProvider)
                }

            // Настройка захвата изображения
            imageCapture = ImageCapture.Builder()
                .build()

            // Установка использования превью и захвата изображения
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStart() {
        super.onStart()

        Log.d("myLog1", "start")
    }

    override fun onResume() {
        super.onResume()
        Log.d("myLog2", "resume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("myLog3", "pause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("myLog4", "stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("myLog5", "destroy")
    }

}