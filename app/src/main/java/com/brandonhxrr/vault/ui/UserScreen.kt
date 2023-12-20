package com.brandonhxrr.vault.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brandonhxrr.vault.data.loadPublicKeyFromFile
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val alertVisible = remember { mutableStateOf(false) }
    val alertMessage = remember { mutableStateOf("") }
    var alertIcon = remember { mutableStateOf("") }
    val publicKeyContent = loadPublicKeyFromFile(context)
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        navController!!.navigateUp()
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Perfil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlideImage(
            model = currentUser?.photoUrl,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = currentUser?.displayName ?: "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentUser?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            SelectionContainer {
                Text(
                    text = publicKeyContent,
                    modifier = Modifier.padding(end = 48.dp),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(
                                "Public Key",
                                publicKeyContent
                            )
                        )
                        Toast
                            .makeText(
                                context,
                                "Llave pública copiada al portapapeles",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    .padding(8.dp)
            )

            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable {
                        val publicKeyFile = File(context.filesDir, "public_key.pem")
                        val destinationFile = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            "public_key.pem"
                        )

                        Log.d("UserScreen", "publicKeyFile: ${destinationFile.path}")

                        try {
                            FileInputStream(publicKeyFile).use { input ->
                                FileOutputStream(destinationFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            alertIcon.value = "success"
                            alertMessage.value = "Llave pública guardada en ${destinationFile.path}"
                        } catch (e: Exception) {
                            alertIcon.value = "error"
                            alertMessage.value = "Error al guardar la llave pública en descargas"
                            e.printStackTrace()
                        }

                        alertVisible.value = true
                    }
                    .padding(8.dp)
            )

        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val publicKeyFile = File(context.filesDir, "private_key.pem")
                val destinationFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "private_key.pem"
                )

                try {
                    FileInputStream(publicKeyFile).use { input ->
                        FileOutputStream(destinationFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    alertMessage.value = "Llave privada guardada en ${destinationFile.path}"
                    alertIcon.value = "success"
                } catch (e: Exception) {
                    alertMessage.value = "Error al guardar la llave pública en descargas"
                    alertIcon.value = "error"
                    e.printStackTrace()
                }

                alertVisible.value = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Guardar llave privada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Firebase.auth.signOut()
                navController!!.navigate(Screens.LoginScreen.name) {
                    popUpTo(Screens.HomeScreen.name) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Rounded.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cerrar sesión")
        }

        if (alertVisible.value) {
            AlertDialog(
                onDismissRequest = {
                    alertVisible.value = false
                },
                text = {
                    Text(text = alertMessage.value)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            alertVisible.value = false
                        }
                    ) {
                        Text(text = "Aceptar")
                    }
                },
                icon = {
                    if (alertIcon.value == "error") {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}
