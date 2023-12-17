package com.brandonhxrr.vault.ui

import android.util.Patterns
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navController: NavController? = null) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var repeatPasswordHidden by rememberSaveable { mutableStateOf(true) }
    var passwordSecurity by rememberSaveable { mutableStateOf(PasswordSecurity.NONE) }

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
            text = stringResource(id = R.string.sign_up),
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
            onValueChange = { user = it },
            label = { Text(text = stringResource(id = R.string.user)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordSecurity = evaluatePasswordSecurity(it)
            },
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
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
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.repeat_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation =
            if (repeatPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { repeatPasswordHidden = !repeatPasswordHidden }, content = {
                    val visibilityIcon =
                        if (repeatPasswordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (repeatPasswordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                })
            },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.Gray) }
        )

        if (password.isNotEmpty()) {
            PasswordSecurityIndicator(passwordSecurity)
        }


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (validateInputs(user, password, repeatPassword)) {
                    // Lógica de registro exitoso
                    keyboardController?.hide()
                } else {
                    // Mostrar errores
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
                    navController?.navigate(Screens.LoginScreen.name)
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


// Función para validar los campos de entrada
private fun validateInputs(user: String, password: String, repeatPassword: String): Boolean {
    return user.isNotBlank() && isValidEmail(user) &&
            password.isNotBlank() && evaluatePasswordSecurity(password) != PasswordSecurity.NONE &&
            password == repeatPassword
}


@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp()
}