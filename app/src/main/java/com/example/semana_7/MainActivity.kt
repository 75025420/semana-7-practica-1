package com.example.semana_7

import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PantallaUsuarios()
        }
    }
}

// MODELO DE DATOS
data class Usuario(
    val nombre: String,
    val apellido: String,
    val foto: String
)

// PANTALLA PRINCIPAL
@Composable
fun PantallaUsuarios() {

    var usuarios by remember {
        mutableStateOf<List<Usuario>>(emptyList())
    }

    LaunchedEffect(Unit) {
        usuarios = obtenerUsuarios()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(usuarios) { usuario ->
            ItemUsuario(usuario)
        }
    }
}

// ITEM DE CADA USUARIO
@Composable
fun ItemUsuario(usuario: Usuario) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AsyncImage(
            model = usuario.foto,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Column {
            Text(text = usuario.nombre)
            Text(text = usuario.apellido)
        }
    }
}

// OBTENER USUARIOS DEL API
suspend fun obtenerUsuarios(): List<Usuario> {

    return withContext(Dispatchers.IO) {

        try {

            val url = URL("https://randomuser.me/api/?results=20")

            val respuesta = url.readText()

            val json = JSONObject(respuesta)

            val resultados = json.getJSONArray("results")

            val lista = mutableListOf<Usuario>()

            for (i in 0 until resultados.length()) {

                val usuarioJson = resultados.getJSONObject(i)

                val nombreJson = usuarioJson.getJSONObject("name")

                val fotoJson = usuarioJson.getJSONObject("picture")

                val usuario = Usuario(
                    nombre = nombreJson.getString("first"),
                    apellido = nombreJson.getString("last"),
                    foto = fotoJson.getString("medium")
                )

                lista.add(usuario)
            }

            lista

        } catch (e: Exception) {
            emptyList()
        }
    }
}