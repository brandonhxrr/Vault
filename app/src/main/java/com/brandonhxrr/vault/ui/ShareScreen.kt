package com.brandonhxrr.vault.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.brandonhxrr.vault.data.EmployeesViewModel
import com.brandonhxrr.vault.data.User
import com.brandonhxrr.vault.data.encryptFileAesGcm
import com.brandonhxrr.vault.data.loadPrivateKeyFromFile
import com.brandonhxrr.vault.data.loadPublicKeyFromFile
import com.brandonhxrr.vault.data.performECDHKeyExchange
import com.brandonhxrr.vault.data.signECDSA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.spec.SecretKeySpec

@Composable
fun Share(modifier: Modifier) {
    val context = LocalContext.current
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val usersViewModel = remember { EmployeesViewModel() }
    val users by usersViewModel.users.collectAsState(initial = emptyList())
    var selectedFile: File? by remember { mutableStateOf(null) }
    var selectedFileUri by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showUploadIndicator by remember { mutableStateOf(false) }

    val selectFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedFile = uri?.let { documentUri ->
                val documentFile = DocumentFile.fromSingleUri(context, documentUri)
                File(documentFile?.name ?: "archivo_desconocido")
            }
            selectedFileUri = uri.toString()
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
    ) {

        Text(
            text = "Compartir archivo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable {
                    isMenuExpanded = !isMenuExpanded
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selectedUser?.email ?: "Seleccionar usuario")
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Toggle Dropdown"
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                expanded = isMenuExpanded,
                onDismissRequest = {
                    isMenuExpanded = false
                }
            ) {
                users.forEach { user ->
                    DropdownMenuItem(
                        onClick = {
                            selectedUser = user
                            isMenuExpanded = false
                        },
                        text = {
                            Text(text = user.email)
                        }
                    )
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                isMenuExpanded = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = selectedFile?.name ?: "")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                selectFileLauncher.launch("*/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Outlined.CloudUpload, contentDescription = "Select File")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Seleccionar archivo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showUploadIndicator = true
                if (selectedUser != null && selectedFile != null) {

                    val userId = selectedUser?.id
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    val privateKey = loadPrivateKeyFromFile(context)
                    val decodedPrivateKey = Base64.getDecoder().decode(privateKey)
                    val otherPartyPublicKey = Base64.getDecoder().decode(selectedUser?.publicKey)

                    val publicKey = loadPublicKeyFromFile(context)

                    val sharedKey = performECDHKeyExchange(decodedPrivateKey, otherPartyPublicKey)

                    if (currentUser != null) {

                        val contentResolver = context.contentResolver
                        val inputStream =
                            contentResolver.openInputStream(selectedFileUri.toUri())

                        val tempFile =
                            File.createTempFile("temp", selectedFile!!.extension)
                        tempFile.outputStream().use { output ->
                            inputStream?.copyTo(output)
                        }

                        val encryptedFile =
                            encryptFileAesGcm(context, sharedKey, tempFile)

                        val signature = signECDSA(decodedPrivateKey, inputStream!!.readBytes())

                        val storage = FirebaseStorage.getInstance()

                        val storageRef: StorageReference = storage.reference

                        val fileId = UUID.randomUUID().toString()

                        val fileRef: StorageReference =
                            storageRef.child("files/$fileId")

                        val uploadTask = fileRef.putFile(Uri.fromFile(encryptedFile))

                        val dateFormat =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val currentDate = dateFormat.format(Date())

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            fileRef.downloadUrl
                        }.addOnCompleteListener { downloadUrlTask ->
                            if (downloadUrlTask.isSuccessful) {
                                val downloadUri = downloadUrlTask.result

                                Log.d("ShareScreen", "downloadUri: $downloadUri")

                                val fileReference = FirebaseDatabase.getInstance()
                                    .getReference("users_private_data")
                                    .child(userId!!).child("shared")
                                    .child(currentUser.uid).child("files").child(fileId)

                                val fileData = hashMapOf(
                                    "path" to downloadUri.toString(),
                                    "name" to selectedFile?.name,
                                    "type" to selectedFile?.extension,
                                    "date" to currentDate.toString(),
                                    "signature" to signature,
                                    "author" to currentUser.uid,
                                    "fileAuthor" to currentUser.displayName,
                                    "authorPublicKey" to publicKey,
                                )

                                fileReference.setValue(fileData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Archivo compartido exitosamente con ${selectedUser?.email}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showUploadIndicator = false
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error al agregar la ruta del archivo en la base de datos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showUploadIndicator = false
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al subir el archivo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showUploadIndicator = false
                            }

                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Selecciona un usuario y un archivo antes de compartir",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Compartir")
        }
        
        if (showUploadIndicator) {
            AlertDialog(
                onDismissRequest = {
                },
                title = {
                    Text(text = "Compartiendo archivo")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Se está cifrando, firmando y subiendo el archivo")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {

                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShareScreenPreview() {
    Share(modifier = Modifier)
}
