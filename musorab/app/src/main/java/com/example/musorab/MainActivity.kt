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
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.musorab.databinding.ActivityMain2Binding
import com.example.musorab.databinding.ActivityMainBinding
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.ExperimentalGetImage
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.*
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageProxy.*
import java.io.File
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.SocketTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Bitmap.Config

//var A:Int=1
class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    lateinit var binding: ActivityMainBinding
    val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var simbol: TextView
    data class ImageData(val imageBase64: String) // Класс для хранения данных изображения
    data class ResponseData(val simbol: String) // Класс для принятия символа с сервера
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("myLog0", "create")
        simbol=binding.result
        val trustAllCerts = arrayOf<TrustManager>(object: X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        })

        // Устанавливаем менеджер который доверяет всем подключениям
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

        // Create a new HostnameVerifier that returns true for all hostnames
        val allHostsValid = HostnameVerifier { _, _ -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)

        requestCameraPermission()
        setupCamera()

    }
    fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    // Здесь предполагается, что imageCaptureCallback - это ваш обратный вызов, который вызывается после успешного захвата изображения
    val imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            try {
                val CAP_WIDTH = 640
                // Получение Bitmap из ImageProxy
                val imageBitmap = imageProxyToBitmap(image)

                val height = image.height
                val width = image.width
                val aspectRatio = width.toFloat() / height

                val CAP_HEIGHT = (CAP_WIDTH / aspectRatio).toInt()

                // Изменение размера изображения
                val resizedImage = Bitmap.createScaledBitmap(imageBitmap!!, CAP_WIDTH, CAP_HEIGHT, true)

                // Получение массива байтов из Bitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                resizedImage?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()

                // Преобразование массива байтов в строку base64
                val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                // Создание экземпляра ImageData
                val imageData = ImageData(base64Image)

                // Преобразование объекта данных в JSON
                val gson = Gson()
                val jsonData = gson.toJson(imageData)

                Log.d(TAG, "JSON data: $jsonData")

                // Отправка JSON на сервер (здесь предполагается использование вашего метода отправки POST-запроса)
                sendDataToServer(jsonData)
            } catch (ex: Exception) {
                // Обработка ошибки при преобразовании или отправке данных
                Log.e(TAG, "Error processing image capture", ex)
            } finally {
                image.close() // Закрытие ImageProxy
            }
        }

        override fun onError(exception: ImageCaptureException) {
            // Обработка ошибки захвата изображения
            Log.e(TAG, "Image capture error", exception)
        }
    }

    fun sendDataToServer(jsonData: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://192.168.195.160")
                val connection = url.openConnection() as HttpURLConnection
                Log.d(TAG, "after string: $jsonData")
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json") // Установка типа содержимого как JSON
                connection.connectTimeout = 10_000 // 10 секунд
                connection.readTimeout = 10_000 // 10 секунд
                connection.doOutput = true

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonData)
                writer.flush()

                val response = StringBuilder()
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?

                while (reader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                reader.close()

                val result = response.toString()
                val gson = Gson()
                val resp = gson.fromJson(result, ResponseData::class.java)
                withContext(Dispatchers.Main) {
                    simbol.text = resp.simbol
                }
            } catch (e: IOException) {
                Log.e("MainActivity", "Ошибка при выполнении POST запроса: ${e.message}")
                "Error: ${e.message}"
                withContext(Dispatchers.Main) {
                    simbol.text = "No connection"
                    end(this@MainActivity)
                }
            } catch (e: SocketTimeoutException) {
                Log.e("MainActivity", "Превышено время ожидания ответа от сервера: ${e.message}")
                withContext(Dispatchers.Main) {
                    // Здесь ты можешь обновить пользовательский интерфейс или выполнить другие действия при возникновении исключения
                    simbol.text = "NOT"
                    end(this@MainActivity)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Неизвестная ошибка: ${e.message}")
                withContext(Dispatchers.Main) {
                    simbol.text = "Неизвестная ошибка: ${e.message}"
                    end(this@MainActivity)
                }
            }
        }

    }
    private fun end(context: Context)
    {
        startActivity(Intent(context, MainActivity2::class.java))
        finish()
    }
    private fun setupCamera() {
        // Создание нового экземпляра CameraX
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Настройка превью
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.Camera1.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Установка использования превью и захвата изображения
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

            lifecycleScope.launch {
                startImageCaptureLoop()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private suspend fun startImageCaptureLoop() {
        while (true) {
            try {
                imageCapture?.takePicture(ContextCompat.getMainExecutor(this), imageCaptureCallback)
            } catch (ex: ImageCaptureException) {
                Log.e(TAG, "Error capturing image", ex)
            }
            // Пауза перед следующим захватом
            delay(2000L)
        }
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
                setupCamera()
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