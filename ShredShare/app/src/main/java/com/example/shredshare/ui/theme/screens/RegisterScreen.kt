package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shredshare.R
import com.example.shredshare.ui.theme.themes.ShredShareTheme
import com.example.shredshare.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    role: String = "Customer",
    onRegisterSuccess: (Int, String) -> Unit = { _, _ -> },
    onBackToLoginClick: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val whiteTextStyle = LocalTextStyle.current.copy(color = Color.White)

    LaunchedEffect(uiState.success, uiState.userId) {
        if (uiState.success && uiState.userId != null) {
            onRegisterSuccess(uiState.userId!!, role)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.20f)
                ),
                border = BorderStroke(2.dp, Color(0xFF4169E1))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shareshred_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.register_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            val displayRole = if (role.equals("Owner", true)) {
                stringResource(R.string.owner_role)
            } else {
                stringResource(R.string.customer_role)
            }

            Text(
                text = "Регистрация като: $displayRole",
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xE61E293B)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = { viewModel.onFirstNameChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Име") },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("firstName"),
                        supportingText = {
                            uiState.validationErrors["firstName"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = { viewModel.onLastNameChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Фамилия") },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("lastName"),
                        supportingText = {
                            uiState.validationErrors["lastName"]?.let { Text(it) }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.address_label))
                        },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("address"),
                        supportingText = {
                            uiState.validationErrors["address"]?.let {
                                Text(it)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.email_label))
                        },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("email"),
                        supportingText = {
                            uiState.validationErrors["email"]?.let {
                                Text(it)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.phone_label))
                        },
                        textStyle = whiteTextStyle,
                        isError = uiState.validationErrors.containsKey("phone"),
                        supportingText = {
                            uiState.validationErrors["phone"]?.let {
                                Text(it)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.password_label))
                        },
                        textStyle = whiteTextStyle,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = uiState.validationErrors.containsKey("password"),
                        supportingText = {
                            uiState.validationErrors["password"]?.let {
                                Text(it)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.confirm_password_label))
                        },
                        textStyle = whiteTextStyle,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = uiState.validationErrors.containsKey("confirmPassword"),
                        supportingText = {
                            uiState.validationErrors["confirmPassword"]?.let {
                                Text(it)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        colors = registerTextFieldColors()
                    )

                    uiState.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.register(role) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xDD2196F3),
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.register_button),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            TextButton(onClick = onBackToLoginClick) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),

    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,

    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    disabledLabelColor = Color.White.copy(alpha = 0.5f),
    errorLabelColor = MaterialTheme.colorScheme.error,

    cursorColor = Color.White,
    errorCursorColor = MaterialTheme.colorScheme.error,

    focusedBorderColor = Color(0xFF2196F3),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    disabledBorderColor = Color.White.copy(alpha = 0.3f),
    errorBorderColor = MaterialTheme.colorScheme.error,

    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White.copy(alpha = 0.8f),
    disabledLeadingIconColor = Color.White.copy(alpha = 0.5f),
    errorLeadingIconColor = MaterialTheme.colorScheme.error,

    focusedSupportingTextColor = Color.White.copy(alpha = 0.8f),
    unfocusedSupportingTextColor = Color.White.copy(alpha = 0.7f),
    disabledSupportingTextColor = Color.White.copy(alpha = 0.5f),
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ShredShareTheme {
        Surface {
            RegisterScreen(onRegisterSuccess = { _, _ -> })
        }
    }
}