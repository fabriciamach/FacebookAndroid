package com.example.facebookandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando o Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)

        // Inicializando o CallbackManager
        callbackManager = CallbackManager.Factory.create()

        // Verifica se já existe um token válido
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            navigateToSecondActivity() // Redireciona se o usuário já estiver logado
        }

        // Botão de login com permissão de perfil público e e-mail
        val btLogin: Button = findViewById(R.id.login_button)
        btLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this, listOf("public_profile", "email")
            )
        }

        // Gerenciando o callback de login
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // Login bem-sucedido, obter dados do usuário
                getUserData(loginResult.accessToken)
            }

            override fun onCancel() {
                // Ação ao cancelar o login
            }

            override fun onError(exception: FacebookException) {
                // Ação ao ocorrer um erro no login
                exception.printStackTrace()
            }
        })
    }

    // Redireciona para a SecondActivity
    private fun navigateToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
        finish() // Fecha a MainActivity
    }

    // Função para obter dados do usuário
    private fun getUserData(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                    try {
                        val email = obj?.getString("email")
                        if (email != null) {
                            // E-mail obtido do Facebook, redirecionar
                            navigateToSecondActivity()
                        } else {
                            // Solicitar que o usuário insira o e-mail manualmente
                            promptUserForEmail()
                        }
                    } catch (exception: JSONException) {
                        exception.printStackTrace()
                    }
                }
            }
        )

        // Solicitando os campos necessários
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    // Função para solicitar que o usuário insira o e-mail
    private fun promptUserForEmail() {
        val emailInput = EditText(this)
        emailInput.hint = "Digite seu e-mail"

        val dialog = AlertDialog.Builder(this)
            .setTitle("E-mail necessário")
            .setMessage("Por favor, insira seu e-mail.")
            .setView(emailInput)
            .setPositiveButton("OK") { dialog, _ ->
                val email = emailInput.text.toString()
                if (email.isNotEmpty()) {
                    // Armazenar ou utilizar o e-mail conforme necessário
                    Toast.makeText(this, "E-mail salvo: $email", Toast.LENGTH_SHORT).show()
                    navigateToSecondActivity()
                } else {
                    Toast.makeText(this, "E-mail não pode ser vazio.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    // Gerencia o resultado da atividade de login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
