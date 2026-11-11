package org.druidanet.druidnet.ui.auth

import org.druidanet.druidnet.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.druidanet.druidnet.navigation.NavigationDestination
import org.druidanet.druidnet.navigation.ProfileDestination

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onNavigationButtonClick: (NavigationDestination) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (!uiState.successMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.successMessage!!,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoggedIn) {
                Text(
                    text = "¡Estás dentro!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Correo: ${uiState.currentUserEmail ?: uiState.email}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { onNavigationButtonClick(ProfileDestination) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ir a mi perfil")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar Sesión")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = navigateBack,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Volver")
                }
            } else {
                when (uiState.step) {
                    SignInStep.EMAIL_INPUT -> {
                        Text(
                            text = "Entra en la comunidad de los druidas",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Introduce tu correo electrónico para recibir el código de acceso",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Featured Image
                        Image(
                            painter = painterResource(R.drawable.druids),
                            contentDescription = "Ilustración de un druida y una druidesa",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.sendOtp() },
                            enabled = !uiState.isLoading && uiState.email.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Solicitar código")
                            }
                        }
                    }
                    SignInStep.OTP_INPUT -> {
                        Image(
                            painter = painterResource(R.drawable.introduce_otp_code),
                            contentDescription = "Ilustración de un druida escribiendo un cód´digo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Introduce el código secreto de 6 dígitos enviado a\n${uiState.email}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.otpCode,
                            onValueChange = { viewModel.onOtpChanged(it) },
                            label = { Text("Código numérico") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.verifyOtp() },
                            enabled = !uiState.isLoading && uiState.otpCode.length >= 6,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Verificar código")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.resetToEmailInput() }) {
                            Text("Cambiar correo electrónico")
                        }
                    }
                }

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = navigateBack) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}
