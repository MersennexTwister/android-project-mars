package com.mars.marsandroid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.mars.marsandroid.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val MARS_URL = "http://5.35.82.110/ident100500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val settings = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE)
        if (settings.contains("login")) {
            if (!settings.contains("last")) {
                val editor = settings.edit()
                editor.putString("last", Gson().toJson(FaceList(ArrayDeque())))
                editor.apply()
            }
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        else {
            val editor = settings.edit()
            editor.putString("last", Gson().toJson(FaceList(ArrayDeque())))
            editor.apply()
        }

        binding.button.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()

            getResponse(login, password)
        }
    }

    protected fun getResponse(login: String, password: String) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("login", login)
            .addFormDataPart("password", password)
            .build()

        val request = Request.Builder()
            .url(MARS_URL)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NETWORK", "login failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body!!.string())

                val settings: SharedPreferences = applicationContext.getSharedPreferences("storage", Context.MODE_PRIVATE);
                val editor: SharedPreferences.Editor = settings.edit();

                val res = json.getInt("is")
                val name = json.getString("name")

                if (res < 2) {
                    this@LoginActivity.runOnUiThread {
                        binding.error.visibility = View.VISIBLE
                    }
                    return
                }

                editor.putString("login", binding.login.text.toString());
                editor.putString("password", binding.password.text.toString());
                editor.putString("name", name)
                editor.apply();

                val intent = Intent(applicationContext, MainActivity::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(intent);
            }
        })
    }
}