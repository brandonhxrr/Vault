package com.brandonhxrr.vault.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brandonhxrr.vault.R
import com.brandonhxrr.vault.data.generateKeys

@Composable
fun NoKeys(modifier: Modifier) {

    val context = LocalContext.current
    var messageText by rememberSaveable { mutableStateOf("") }
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("vault_preferences", Context.MODE_PRIVATE)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_found), // Reemplaza con tu recurso de imagen
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aún no has generado tus llaves", // Reemplaza con tu propio texto
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight(400)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                try {
                    generateKeys(context)
                    messageText = "Se generaron y guardaron las llaves exitosamente"
                    sharedPreferences.edit().putBoolean("generated_keys", true).apply()
                } catch (e: Exception) {
                    messageText = "Ocurrió un error al generar las llaves"
                    print(e)
                    Log.e("NoKeysScreen", e.toString())

                }


            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        ) {
            Text(text = "Generar llaves")
        }

        Toast.makeText(context, messageText, Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun NoKeysPreview() {
    NoKeys(modifier = Modifier.fillMaxSize())
}