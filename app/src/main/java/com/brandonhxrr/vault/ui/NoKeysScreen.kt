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
import com.brandonhxrr.vault.data.loadPublicKeyFromFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun NoKeys(modifier: Modifier) {

    val context = LocalContext.current
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
            painter = painterResource(id = R.drawable.not_found),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aún no has generado tus llaves",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight(400)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                try {
                    generateKeys(context)

                    val publicKey = loadPublicKeyFromFile(context)

                    val auth = FirebaseAuth.getInstance()

                    auth.currentUser?.let { currentUser ->
                        val userId = currentUser.uid
                        val userReference =
                            FirebaseDatabase.getInstance().getReference("users_public_data")
                                .child(userId)
                        userReference.child("public_key").setValue(publicKey)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Llaves generadas correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                sharedPreferences.edit().putBoolean("generated_keys", true).apply()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    context,
                                    "Ocurrió un error al subir la clave pública a Firebase",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("NoKeysScreen", exception.toString())
                            }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Ocurrió un error al generar las llaves",
                        Toast.LENGTH_SHORT
                    ).show()
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
    }
}

@Preview(showBackground = true)
@Composable
fun NoKeysPreview() {
    NoKeys(modifier = Modifier.fillMaxSize())
}