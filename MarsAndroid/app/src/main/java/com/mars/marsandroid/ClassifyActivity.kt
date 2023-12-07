package com.mars.marsandroid

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.mars.marsandroid.databinding.ActivityClassifyBinding
import com.mars.marsandroid.databinding.ActivityLoginBinding
import com.mars.marsandroid.databinding.FragmentGalleryBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class ClassifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassifyBinding
    private val CAMERA_REQUEST = 1888
    private val MARS_URL = "http://5.35.82.110/put_mark100500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify)

        binding = ActivityClassifyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.photo.setImageBitmap(intent.getParcelableExtra("bitmap"))

        binding.repeat.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        binding.send.setOnClickListener {
            binding.photo.buildDrawingCache()
            val bitmap = binding.photo.getDrawingCache()
            var mark = "-"

            if (binding.mark.isChecked) {
                mark = "+"
            }

            getResponse(bitmap, mark)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        binding.returning.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun onLogout() {
        val settings = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.remove("login")
        editor.remove("password")
        editor.remove("name")
        editor.remove("last")
        editor.apply()

        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    protected fun addToLast(_btm: Bitmap): Int {
        val settings = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        val set = Gson().fromJson(settings.getString("last", ""), FaceList::class.java)
        val editor = settings.edit()
        if (set.list.size == FaceBuilder.MAX_SIZE) {
            set.list.removeFirst()
        }
        val toRet = set.add(_btm)

        editor.remove("last")
        editor.putString("last", Gson().toJson(set, FaceList::class.java))
        editor.apply()

        return toRet
    }

    protected fun compressImage(bitmap: Bitmap): ByteArray {

        val os = ByteArrayOutputStream()
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

        return os.toByteArray()
    }

    protected fun updateProcess(pid: Int, success: Int, name: String) {
        val settings = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        val set = Gson().fromJson(settings.getString("last", ""), FaceList::class.java)
        val editor = settings.edit()

        for (i in set.list.indices) {
            val el = set.list.get(i)
            if (el.success == 0 && el.process_id == pid) {
                set.list.set(i, Face(success, -1, name, el.btm))
            }
        }

        editor.remove("last")
        editor.putString("last", Gson().toJson(set, FaceList::class.java))
        editor.apply()
    }

    protected fun getResponse(bitmap: Bitmap, mark: String) {
        val file = compressImage(bitmap)
        val settings = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        val login = settings.getString("login", "") as String

        val password = settings.getString("password", "") as String
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("photo", "img.jpg", requestFile)
            .addFormDataPart("mark", mark)
            .addFormDataPart("login", login)
            .addFormDataPart("password", password)
            .build()

        val request = Request.Builder()
            .url(MARS_URL)
            .post(requestBody)
            .build()

        val process = addToLast(bitmap)

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NETWORK", "send failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body!!.string())
                val res = json.getInt("is")
                var name = "\r"
                if (res == 0) {
                    onLogout()
                }
                else if (res == 2) {
                    name = json.getString("name")
                }

                var suc = -1
                if (res == 2) {
                    suc = 1
                }

                updateProcess(process, suc, name)
            }
        })
    }
}