package com.example.musorab

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musorab.databinding.ActivityMain2Binding
import com.example.musorab.databinding.ActivityMainBinding
import android.Manifest

//var A:Int=1
class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    lateinit var binding: ActivityMainBinding
    val REQUEST_CAMERA_PERMISSION = 100

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("myLog0", "create")
        /*if (A==1) {
            A=0
            startActivity(Intent(this, MainActivity2::class.java))
                finish()
        }*/
        requestCameraPermission()
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
    private fun requestCameraPermission() { // Окно, запрашивающее доступ к камере
        // Проверка, нет  ли доступа к камере
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION) // Вызов окна
        }
    }
    // Данная функция нужна для того,что если нет доступа к камере, то выведется другой экран
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Доступ к камере разрешен

            } else {
                // Доступ к камере не разрешен
                startActivity(Intent(this, MainActivity3::class.java))
                finish()
            }
        }
    }
    override fun onStart() { // Эти функции добавлены просто так
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