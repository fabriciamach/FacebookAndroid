package com.example.facebookandroid

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import org.json.JSONException
import org.json.JSONObject

class SecondActivity : ComponentActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView // Adicionando ImageView para mostrar a imagem selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        val nome: TextView = findViewById(R.id.name)
        val btLogout: Button = findViewById(R.id.but_logout)
        val btShare: Button = findViewById(R.id.share_but)
        val btSelectImage: Button = findViewById(R.id.select_image_but)
        imageView = findViewById(R.id.image_view) // Inicializando a ImageView

        // Obtendo o AccessToken do usuário
        val accessToken = AccessToken.getCurrentAccessToken()

        // Fazendo a requisição para obter os dados do usuário
        val request = GraphRequest.newMeRequest(
            accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                    try {
                        // Obtendo o nome e definindo no TextView
                        val fullName = obj?.getString("name")
                        nome.text = fullName
                    } catch (exception: JSONException) {
                        exception.printStackTrace()
                    }
                }
            }
        )

        // Solicitando os campos id, nome e email
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()

        // Logout do Facebook
        btLogout.setOnClickListener {
            LoginManager.getInstance().logOut()
            startActivity(Intent(this@SecondActivity, MainActivity::class.java))
            finish()
        }

        // Selecionar imagem da galeria
        btSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Dentro de SecondActivity
        btShare.setOnClickListener {
            selectedImageUri?.let { uri ->
                // Criar o SharePhoto usando o URI diretamente
                val photo = SharePhoto.Builder()
                    .setImageUrl(uri) // Mudar para setImageUrl se o URI for acessível
                    .build()

                // Criar o SharePhotoContent
                val content = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()

                // Criar o ShareDialog e compartilhar o conteúdo
                val shareDialog = ShareDialog(this)
                if (ShareDialog.canShow(SharePhotoContent::class.java)) {
                    shareDialog.show(content)
                }
            } ?: run {
                Toast.makeText(this, "Selecione uma imagem primeiro", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            // Exibir a imagem selecionada na ImageView
            selectedImageUri?.let {
                imageView.setImageURI(it)
            }
        }
    }
}
