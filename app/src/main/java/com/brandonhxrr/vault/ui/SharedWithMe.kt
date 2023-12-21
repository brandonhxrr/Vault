package com.brandonhxrr.vault.ui

import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandonhxrr.vault.R
import com.brandonhxrr.vault.data.SharedFile
import com.brandonhxrr.vault.data.decryptFileAesGcm
import com.brandonhxrr.vault.data.loadPrivateKeyFromFile
import com.brandonhxrr.vault.data.performECDHKeyExchange
import com.brandonhxrr.vault.data.signECDSA
import com.brandonhxrr.vault.data.verifyECDSA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Base64

private fun getSharedFilesFromDatabase(onSuccess: (List<SharedFile>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val filesReference = database.reference
        .child("users_private_data")
        .child(currentUser!!.uid)
        .child("shared")

    val sharedFiles = mutableListOf<SharedFile>()

    filesReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (userSnapshot in snapshot.children) {
                val filesNode = userSnapshot.child("files")
                for (fileSnapshot in filesNode.children) {
                    val id = fileSnapshot.key ?: ""
                    val name = fileSnapshot.child("name").getValue(String::class.java) ?: ""
                    val date = fileSnapshot.child("date").getValue(String::class.java) ?: ""
                    val authorId =
                        fileSnapshot.child("author").getValue(String::class.java) ?: ""
                    val fileURL =
                        fileSnapshot.child("path").getValue(String::class.java) ?: ""
                    val type = fileSnapshot.child("type").getValue(String::class.java) ?: ""
                    val signature =
                        fileSnapshot.child("signature").getValue(String::class.java) ?: ""
                    var author = fileSnapshot.child("fileAuthor").getValue(String::class.java)
                        ?: ""
                    var authorPublicKey =
                        fileSnapshot.child("authorPublicKey").getValue(String::class.java)
                            ?: ""

                    val sharedFile = SharedFile(
                        id = id,
                        name = name,
                        date = date,
                        authorId = authorId,
                        fileURL = fileURL,
                        type = type,
                        signature = signature,
                        author = author,
                        authorPublicKey = authorPublicKey
                    )

                    sharedFiles.add(sharedFile)
                }
            }
            onSuccess(sharedFiles)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}

@Composable
fun SharedWithMe(modifier: Modifier) {
    var sharedFiles by remember { mutableStateOf<List<SharedFile>>(emptyList()) }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        getSharedFilesFromDatabase {
            sharedFiles = it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Compartidos conmigo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn(
            state = lazyListState
        ) {
            items(sharedFiles) { file ->
                SharedFile(file)
            }
        }
    }
}

@Composable
fun SharedFile(sharedFile: SharedFile) {
    val context = LocalContext.current
    val alertVisible = remember { mutableStateOf(false) }
    val alertMessage = remember { mutableStateOf("") }
    var alertIcon = remember { mutableStateOf("") }
    var painterResource = remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when(sharedFile.type){
                "pdf" -> {
                    painterResource.value = R.drawable.pdf
                }
                "doc", "docx" -> {
                    painterResource.value = R.drawable.doc
                }
                "xls", "xlsx" -> {
                    painterResource.value = R.drawable.xls
                }
                "ppt", "pptx" -> {
                    painterResource.value = R.drawable.ppt
                }
                "jpg", "jpeg", "png" -> {
                    painterResource.value = R.drawable.image
                }
                "mp3", "wav" -> {
                    painterResource.value = R.drawable.audio
                }
                "mp4", "avi", "mov" -> {
                    painterResource.value = R.drawable.video
                }
                "zip", "rar" -> {
                    painterResource.value = R.drawable.zip
                }
                "js", "html", "css", "php", "java", "kt", "py", "c", "cpp", "h", "cs", "go", "rb", "json", "xml" -> {
                    painterResource.value = R.drawable.code
                }
                else -> {
                    painterResource.value = R.drawable.txt
                }
            }
            Icon(
                painter = painterResource(painterResource.value),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
                    .size(40.dp),
                tint = Color.Unspecified
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = sharedFile.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.product_sans_regular)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = sharedFile.author,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.product_sans_regular))
                )

                Text(
                    text = sharedFile.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.product_sans_regular))
                )

            }

            Icon(
                imageVector = Icons.Rounded.CheckCircleOutline,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
                    .size(28.dp)
                    .clickable {
                        try {
                            val privateKey = loadPrivateKeyFromFile(context)
                            val decodedPrivateKey = Base64
                                .getDecoder()
                                .decode(privateKey)


                            Thread {
                                try {
                                    val inputFile = File(
                                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                                        sharedFile.name
                                    )

                                    if (inputFile.exists()) {
                                        try {
                                            val signature =
                                                signECDSA(decodedPrivateKey, inputFile.readBytes())
                                            val decodedSignature = Base64
                                                .getDecoder()
                                                .decode(signature)

                                            val verification = verifyECDSA(
                                                Base64
                                                    .getDecoder()
                                                    .decode(sharedFile.authorPublicKey),
                                                inputFile.readBytes(),
                                                decodedSignature
                                            )

                                            GlobalScope.launch(Dispatchers.Main) {
                                                if (verification) {
                                                    alertMessage.value = "El archivo es auténtico"
                                                    alertIcon.value = "success"
                                                    alertVisible.value = true
                                                } else {
                                                    alertMessage.value =
                                                        "El archivo no es auténtico"
                                                    alertIcon.value = "error"
                                                    alertVisible.value = true
                                                }
                                            }
                                        } catch (e: Exception) {
                                            GlobalScope.launch(Dispatchers.Main) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Error al verificar el archivo",
                                                        Toast.LENGTH_LONG
                                                    )
                                                    .show()
                                                Log.e("SharedWithMe", e.stackTraceToString())
                                            }
                                        }
                                    } else {
                                        GlobalScope.launch(Dispatchers.Main) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Primero descarga el archivo",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }

                                } catch (e: Exception) {
                                    GlobalScope.launch(Dispatchers.Main) {
                                        Log.e("SharedWithMe1", e.stackTraceToString())
                                    }
                                }
                            }.start()
                        } catch (e: Exception) {
                            Log.e("SharedWithMe3", e.stackTraceToString())
                        }

                    }
            )

            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f)
                    .size(28.dp)
                    .clickable {
                        try {

                            val decodedPublicKey = Base64
                                .getDecoder()
                                .decode(sharedFile.authorPublicKey)

                            val privateKey = loadPrivateKeyFromFile(context)
                            val decodedPrivateKey = Base64
                                .getDecoder()
                                .decode(privateKey)
                            val sharedKey =
                                performECDHKeyExchange(decodedPrivateKey, decodedPublicKey)

                            try {
                                Log.d("SharedWithMe", sharedFile.fileURL)
                                val url = URL(sharedFile.fileURL)

                                Thread {
                                    try {
                                        val connection = url.openConnection()
                                        connection.connect()

                                        val input = BufferedInputStream(url.openStream())

                                        try {
                                            val decryptedData = decryptFileAesGcm(
                                                sharedKey,
                                                input.readBytes()
                                            )

                                            val destinationFile = File(
                                                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                                                sharedFile.name
                                            )

                                            val outputStream = FileOutputStream(destinationFile)
                                            outputStream.write(decryptedData)
                                            outputStream.close()

                                            GlobalScope.launch(Dispatchers.Main) {
                                                alertMessage.value =
                                                    "Archivo descargado y guardado con éxito en: ${destinationFile.absolutePath}"
                                                alertIcon.value = "success"
                                                alertVisible.value = true
                                            }
                                        } catch (e: Exception) {
                                            GlobalScope.launch(Dispatchers.Main) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Error al descargar o guardar el archivo",
                                                        Toast.LENGTH_LONG
                                                    )
                                                    .show()
                                                Log.e("SharedWithMe", e.stackTraceToString())
                                            }
                                        } finally {
                                            input.close()
                                        }
                                    } catch (e: Exception) {
                                        GlobalScope.launch(Dispatchers.Main) {
                                            Log.e("SharedWithMe1", e.stackTraceToString())
                                        }
                                    }
                                }.start()
                            } catch (e: Exception) {
                                Log.e("SharedWithMe2", e.stackTraceToString())
                            }
                        } catch (e: Exception) {
                            Log.e("SharedWithMe4", e.stackTraceToString())
                        }
                    }
            )

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
}

@Preview(showBackground = true)
@Composable
fun SharedWithMePreview() {
    SharedWithMe(modifier = Modifier)
}
