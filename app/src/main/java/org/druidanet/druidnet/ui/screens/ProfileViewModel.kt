package org.druidanet.druidnet.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.druidanet.druidnet.model.Profile
import javax.inject.Inject

@Serializable
data class ProfileUiState(
    val profile: Profile? = null,
    val user: UserInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow (ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        retrieveProfileData()
    }

    private fun retrieveProfileData() {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = supabase.auth.currentSessionOrNull()?.user ?: return@launch

                Log.i("ProfileViewModel", "User: $user")

                val respProfile = supabase.from("profiles").select {
                    filter {
                        eq("user_id", user.id)
                    }
                }.decodeSingle<Profile>()

                _uiState.update { it.copy(profile = respProfile, user = user, isLoading = false) }
            } catch (e: RestException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                Log.e("ProfileViewModel", "Error de Supabase al obtener los datos del perfil: $e")
            } catch (e: AuthRestException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                Log.e("ProfileViewModel", "Error de autenticación de Supabase al obtener los datos del perfil: $e")
            }
        }
    }
}
