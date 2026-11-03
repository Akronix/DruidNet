package org.druidanet.druidnet.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SignInStep {
    EMAIL_INPUT,
    OTP_INPUT
}

data class SignInUiState(
    val email: String = "",
    val otpCode: String = "",
    val step: SignInStep = SignInStep.EMAIL_INPUT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val currentUserEmail: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: Auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    init {
        checkCurrentSession()
    }

    private fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val session = auth.currentSessionOrNull()
                if (session != null) {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            currentUserEmail = session.user?.email
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore session check errors
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null, successMessage = null) }
    }

    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otpCode = otp, errorMessage = null, successMessage = null) }
    }

    fun sendOtp() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Por favor, introduce un correo electrónico válido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                auth.signInWith(OTP) {
                    this.email = email
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = SignInStep.OTP_INPUT,
                        successMessage = "¡Código de acceso enviado a $email!"
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al enviar el código OTP: $e" )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Error al enviar el código OTP."
                    )
                }
            }
        }
    }

    fun verifyOtp() {
        val email = _uiState.value.email.trim()
        val otp = _uiState.value.otpCode.trim()
        if (otp.length < 6) {
            _uiState.update { it.copy(errorMessage = "Introduce el código de 6 dígitos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = email,
                    token = otp
                )
                val session = auth.currentSessionOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUserEmail = session?.user?.email ?: email,
                        successMessage = "¡Autenticación exitosa!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Código OTP inválido o expirado."
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _uiState.update {
                    SignInUiState()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    fun resetToEmailInput() {
        _uiState.update {
            it.copy(
                step = SignInStep.EMAIL_INPUT,
                otpCode = "",
                errorMessage = null,
                successMessage = null
            )
        }
    }
}
