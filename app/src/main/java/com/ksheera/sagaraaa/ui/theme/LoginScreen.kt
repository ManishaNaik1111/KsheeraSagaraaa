package com.ksheera.sagaraaa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ksheera.sagaraaa.viewmodel.AuthViewModel
import com.ksheera.sagaraaa.AppStrings

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val lang = AppStrings.currentLanguage

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var showNoAccountDialog by remember { mutableStateOf(false) }
    val authMessage by authViewModel.authMessage.collectAsState()

    LaunchedEffect(authMessage) {
        when (authMessage) {
            "LOGIN_SUCCESS" -> {
                onLoginSuccess()
                authViewModel.clearMessage()
            }
            "NO_ACCOUNT" -> {
                showNoAccountDialog = true
                authViewModel.clearMessage()
            }
        }
    }

    // No Account Dialog
    if (showNoAccountDialog) {
        Dialog(onDismissRequest = { showNoAccountDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        AppStrings.get("no_account_title"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        AppStrings.get("no_account_desc"),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showNoAccountDialog = false
                            onNavigateToRegister()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(AppStrings.get("create_account"), color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showNoAccountDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotDialog = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🐄", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "KsheeraSagara",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "\u0C95\u0CCD\u0CB7\u0CC0\u0CB0\u0CB8\u0CBE\u0C97\u0CB0 | Dairy Profit Calculator",
                fontSize = 13.sp,
                color = Color(0xFFB9F6CA)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        AppStrings.get("welcome_title"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        AppStrings.get("sign_in"),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(AppStrings.get("email")) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(AppStrings.get("password")) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(
                        onClick = { showForgotDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            AppStrings.get("forgot_password"),
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp
                        )
                    }

                    // Show error message
                    if (authMessage.isNotEmpty() &&
                        authMessage != "NO_ACCOUNT" &&
                        authMessage != "LOGIN_SUCCESS"
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authMessage,
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            authViewModel.login(email.trim(), password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            AppStrings.get("login"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            AppStrings.get("dont_have_account"),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        TextButton(onClick = onNavigateToRegister) {
                            Text(
                                AppStrings.get("create_account"),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "🔑 Forgot Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your password is saved locally on this device. " +
                            "If you forgot it, you will need to reinstall the app and create a new account.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                ) {
                    Text(
                        "💡 Tip: Your password is stored securely on your device only.",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        color = Color(0xFFF57F17)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("OK, Got it", color = Color.White)
                }
            }
        }
    }
}