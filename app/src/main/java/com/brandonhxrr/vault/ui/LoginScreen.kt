package com.brandonhxrr.vault.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavController? = null) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    var errorText by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(72.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(alignment = Alignment.CenterVertically),
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight(400)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(alignment = Alignment.CenterHorizontally),
            color = Color.Black,
            fontSize = 40.sp
        )

        Spacer(modifier = Modifier.height(50.dp))

        OutlinedTextField(
            value = user,
            onValueChange = {user = it},
            label = { Text(text = stringResource(id = R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.Gray) }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }, content = {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                })
            },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.Gray) }
        )


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                keyboardController?.hide()

                auth.signInWithEmailAndPassword(user, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController?.navigate(Screens.HomeScreen.name){
                                popUpTo(Screens.LoginScreen.name){
                                    inclusive = true
                                }
                            }
                        } else {
                            // Si hay un error en la autenticación, maneja los posibles casos
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidUserException) {
                                // El usuario no existe
                                errorText = "Usuario no encontrado"

                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                // Credenciales inválidas (por ejemplo, contraseña incorrecta)
                                errorText = "Credenciales inválidas"
                            } catch (e: Exception) {
                                // Otros errores
                                errorText = "Error en la autenticación"
                            }
                            Toast.makeText(
                                context,
                                errorText,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier
                .height(50.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.login))
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row (
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Text(
                text = stringResource(id = R.string.no_account),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(end = 8.dp),
                fontFamily = FontFamily(Font(R.font.product_sans_regular))
            )
            Text(
                text = stringResource(id = R.string.sign_up),
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController?.navigate(Screens.SignUpScreen.name){
                        popUpTo(Screens.LoginScreen.name){
                            inclusive = true
                        }
                    }
                },
                fontFamily = FontFamily(Font(R.font.product_sans_regular))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}
