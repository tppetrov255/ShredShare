package com.example.shredshare.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shredshare.R
import com.example.shredshare.ui.theme.components.AppScreenHeader
import com.example.shredshare.viewmodel.ProfileViewModel

@Composable
fun ProfileRoute(
    userId: Int,
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    ProfileScreen(
        uiState = uiState,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onAddressChange = viewModel::onAddressChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        onSaveClick = { viewModel.saveProfile(userId) },
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onBookingsClick = onBookingsClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileViewModel.ProfileUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            AppScreenHeader(
                title = "Профил",
                onBackClick = onBackClick,
                onProfileClick = {},
                onCartClick = onCartClick,
                onBookingsClick = onBookingsClick,
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xE61E293B)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Редакция на профил",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Роля: ${uiState.role}",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 14.sp
                            )

                            OutlinedTextField(
                                value = uiState.firstName,
                                onValueChange = onFirstNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Име") },
                                singleLine = true,
                                colors = profileTextFieldColors()
                            )

                            OutlinedTextField(
                                value = uiState.lastName,
                                onValueChange = onLastNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Фамилия") },
                                singleLine = true,
                                colors = profileTextFieldColors()
                            )

                            OutlinedTextField(
                                value = uiState.address,
                                onValueChange = onAddressChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Адрес") },
                                singleLine = true,
                                colors = profileTextFieldColors()
                            )

                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = onEmailChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Имейл") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = profileTextFieldColors()
                            )

                            OutlinedTextField(
                                value = uiState.phone,
                                onValueChange = onPhoneChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Телефон") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = profileTextFieldColors()
                            )

                            uiState.error?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            uiState.message?.let {
                                Text(
                                    text = it,
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onSaveClick,
                                enabled = !uiState.isSaving,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xDD2196F3),
                                    contentColor = Color.White
                                )
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.height(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Запази промените",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.White.copy(alpha = 0.08f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
    focusedBorderColor = Color(0xFF2196F3),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.75f),
    cursorColor = Color.White
)