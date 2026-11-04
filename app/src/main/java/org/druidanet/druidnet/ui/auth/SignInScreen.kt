package org.druidanet.druidnet.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    SignInScreenContent(
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onOtpChanged = viewModel::onOtpChanged,
        onSendOtp = viewModel::sendOtp,
        onVerifyOtp = viewModel::verifyOtp,
        onSignOut = viewModel::signOut,
        onResetToEmailInput = viewModel::resetToEmailInput,
        navigateBack = navigateBack,
        modifier = modifier
    )
}

@Composable
fun SignInScreenContent(
    uiState: SignInUiState,
    onEmailChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onSignOut: () -> Unit,
    onResetToEmailInput: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val minHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight - 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.05f))
                // Top Section: Title
                Text(
                    text = "Entra en la comunidad de los druidas",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Featured Image filling all remaining available height
                Image(
                    painter = painterResource(R.drawable.druids),
                    contentDescription = "Ilustración de un druida y una druidesa",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Form / Controls Section at the bottom
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoggedIn) {
                        Text(
                            text = "¡Estás dentro!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correo: ${uiState.currentUserEmail ?: uiState.email}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onSignOut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cerrar Sesión")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = navigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Volver")
                        }
                    } else {
                        Text(
                            text = "Introduce tu correo electrónico para recibir el código de acceso",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        when (uiState.step) {
                            SignInStep.EMAIL_INPUT -> {
                                OutlinedTextField(
                                    value = uiState.email,
                                    onValueChange = onEmailChanged,
                                    label = { Text("Correo electrónico") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onSendOtp,
                                    enabled = !uiState.isLoading && uiState.email.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                                    } else {
                                        Text("Enviar código")
                                    }
                                }
                            }
                            SignInStep.OTP_INPUT -> {
                                Text(
                                    text = "Introduce el código de 6 dígitos enviado a\n${uiState.email}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = uiState.otpCode,
                                    onValueChange = onOtpChanged,
                                    label = { Text("Código OTP") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onVerifyOtp,
                                    enabled = !uiState.isLoading && uiState.otpCode.length >= 6,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                                    } else {
                                        Text("Verificar código")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = onResetToEmailInput) {
                                    Text("Cambiar correo electrónico")
                                }
                            }
                        }

                        val errorMessage = uiState.errorMessage
                        if (!errorMessage.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        val successMessage = uiState.successMessage
                        if (!successMessage.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = successMessage,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = navigateBack) {
                            Text("Volver al inicio")
                        }
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Pixel 10",
    showBackground = true,
    device = "id:pixel_10"
)
@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    DruidNetTheme {
        SignInScreenContent(
            uiState = SignInUiState(email = "druid@druidanet.org"),
            onEmailChanged = {},
            onOtpChanged = {},
            onSendOtp = {},
            onVerifyOtp = {},
            onSignOut = {},
            onResetToEmailInput = {},
            navigateBack = {}
        )
    }
}
