package com.brandonhxrr.vault.ui

import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brandonhxrr.vault.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun SignUp(navController: NavController? = null) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var repeatPasswordHidden by rememberSaveable { mutableStateOf(true) }
    var passwordSecurity by rememberSaveable { mutableStateOf(PasswordSecurity.NONE) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val auth = FirebaseAuth.getInstance()
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    var errorText by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val registerImageActivityLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri.toString()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(alignment = Alignment.CenterVertically),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight(400)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.sign_up),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(alignment = Alignment.CenterHorizontally),
            color = Color.Black,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .clickable {
                registerImageActivityLauncher.launch("image/*")
            }
            .align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                GlideImage(
                    model = selectedImageUri,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(stringResource(id = R.string.user)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                // Handle Done button press
                focusManager.clearFocus()
                keyboardController?.hide()
            }),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(value = user,
            onValueChange = { user = it },
            label = { Text(text = stringResource(id = R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Mail, contentDescription = null, tint = Color.Gray
                )
            })

        OutlinedTextField(value = password,
            onValueChange = {
                password = it
                passwordSecurity = evaluatePasswordSecurity(it)
            },
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
            ),
            visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }, content = {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                })
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Lock, contentDescription = null, tint = Color.Gray
                )
            })
        OutlinedTextField(value = repeatPassword,
            onValueChange = { repeatPassword = it },
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.repeat_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            visualTransformation = if (repeatPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { repeatPasswordHidden = !repeatPasswordHidden }, content = {
                    val visibilityIcon =
                        if (repeatPasswordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (repeatPasswordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                })
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Lock, contentDescription = null, tint = Color.Gray
                )
            })

        if (password.isNotEmpty()) {
            PasswordSecurityIndicator(passwordSecurity)
            Spacer(modifier = Modifier.height(8.dp))
            val passwordStrengthMessage = when (passwordSecurity) {
                PasswordSecurity.WEAK -> "La contraseña es débil"
                PasswordSecurity.MODERATE -> "La contraseña es moderada"
                PasswordSecurity.STRONG -> "La contraseña es fuerte"
                else -> ""
            }
            if (passwordStrengthMessage.isNotEmpty()) {
                Text(
                    text = passwordStrengthMessage,
                    color = when (passwordSecurity) {
                        PasswordSecurity.WEAK -> Color.Red
                        PasswordSecurity.MODERATE -> Color.Yellow
                        PasswordSecurity.STRONG -> Color.Green
                        else -> Color.Transparent
                    },
                    modifier = Modifier.padding(start = 32.dp),
                    fontFamily = FontFamily(Font(R.font.product_sans_regular))
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (validateInputs(user, password, repeatPassword, context)) {
                    keyboardController?.hide()

                    auth.createUserWithEmailAndPassword(user, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Subir la imagen a Firebase Storage
                                if(selectedImageUri != null){
                                    val imageRef = storageRef.child("profile_images/${auth.currentUser?.uid}")
                                    val uploadTask = imageRef.putFile(Uri.parse(selectedImageUri))

                                    uploadTask.continueWithTask { task ->
                                        if (!task.isSuccessful) {
                                            task.exception?.let {
                                                throw it
                                            }
                                        }
                                        imageRef.downloadUrl
                                    }.addOnCompleteListener { downloadUrlTask ->
                                        if (downloadUrlTask.isSuccessful) {
                                            // Actualizar el perfil del usuario con la URL de la imagen
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName)
                                                .setPhotoUri(downloadUrlTask.result)
                                                .build()

                                            auth.currentUser?.updateProfile(profileUpdates)

                                            navController?.navigate(Screens.HomeScreen.name){
                                                popUpTo(Screens.SignUpScreen.name){
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            // Manejar error al obtener la URL de la imagen
                                        }
                                    }
                                }else{
                                    navController?.navigate(Screens.HomeScreen.name){
                                        popUpTo(Screens.SignUpScreen.name){
                                            inclusive = true
                                        }
                                    }
                                }
                                // Lógica de registro exitoso
                                keyboardController?.hide()
                            } else {
                                // Mostrar errores de Firebase
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    // Email ya registrado
                                    // Muestra el mensaje de error en el campo de texto o usa Toast
                                    errorText = "Correo ya registrado"
                                } catch (e: Exception) {
                                    // Otros errores de Firebase
                                    // Muestra el mensaje de error en el campo de texto o usa Toast
                                    errorText = "Error en el registro"
                                }

                                Toast.makeText(
                                    context,
                                    errorText,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ), modifier = Modifier
                .height(50.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(id = R.string.yes_account),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 8.dp),
                fontFamily = FontFamily(Font(R.font.product_sans_regular))
            )
            Text(
                text = stringResource(id = R.string.login_action),
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController?.navigate(Screens.LoginScreen.name){
                        popUpTo(Screens.SignUpScreen.name){
                            inclusive = true
                        }
                    }
                },
                fontFamily = FontFamily(Font(R.font.product_sans_regular))
            )
        }
    }
}

enum class PasswordSecurity {
    NONE, WEAK, MODERATE, STRONG
}

// Función para evaluar la seguridad de la contraseña
private fun evaluatePasswordSecurity(password: String): PasswordSecurity {
    val minLength = 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

    return when {
        password.length < minLength -> PasswordSecurity.NONE
        !hasUppercase && !hasLowercase && !hasDigit && !hasSpecialChar -> PasswordSecurity.WEAK
        (hasUppercase || hasLowercase) && hasDigit && hasSpecialChar -> PasswordSecurity.STRONG
        else -> PasswordSecurity.MODERATE
    }
}

// Componente para mostrar el indicador de seguridad de la contraseña
@Composable
private fun PasswordSecurityIndicator(passwordSecurity: PasswordSecurity) {
    val color = when (passwordSecurity) {
        PasswordSecurity.WEAK -> Color.Red
        PasswordSecurity.MODERATE -> Color.Yellow
        PasswordSecurity.STRONG -> Color.Green
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(color)
    )
}

// Función para validar el formato de dirección de correo electrónico
private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


private fun validateInputs(user: String, password: String, repeatPassword: String, context: Context): Boolean {
    var isValid = true
    var errorMessage = ""

    if (user.isBlank() || !isValidEmail(user)) {
        errorMessage = "El correo no es válido"
        isValid = false
    } else if (password.isBlank()) {
        errorMessage = "La contraseña no puede estar vacía"
        isValid = false
    } else if (password != repeatPassword) {
        errorMessage = "Las contraseñas no coinciden"
        isValid = false
    } else if (evaluatePasswordSecurity(password) != PasswordSecurity.STRONG){
        errorMessage = "La contraseña debe ser de al menos 8 caracteres, tener al menos una mayúscula, una minúscula, un número y un caracter especial"
    }

    if (!isValid) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    return isValid
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp()
}