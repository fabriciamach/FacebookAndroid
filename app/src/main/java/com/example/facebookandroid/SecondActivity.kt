package com.example.facebookandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import org.json.JSONException
import org.json.JSONObject

class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_activity)

        val nome: TextView = findViewById(R.id.name)
        val btLogout: Button = findViewById(R.id.but_logout)

        val accessToken = AccessToken.getCurrentAccessToken()

        val request = GraphRequest.newMeRequest(
            accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(
                    obj: JSONObject?,
                    response: GraphResponse?
                ) {
                    try {
                        // Obtenha o nome e defina-o no TextView
                        val fullName = obj?.getString("name")
                        nome.text = fullName // Corrigido aqui
                    } catch (exception: JSONException) {
                        exception.printStackTrace()
                    }
                }
            }
        )

        val parameters = Bundle()
        parameters.putString("fields", "id,name,link")
        request.parameters = parameters
        request.executeAsync()

        btLogout.setOnClickListener {
            LoginManager.getInstance().logOut()
            startActivity(Intent(this@SecondActivity, MainActivity::class.java))
            finish()
        }
    }
}
