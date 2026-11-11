package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.druidanet.druidnet.model.Profile

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.profile != null) {
            val profile: Profile = uiState.profile!!

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.TopCenter)
            ) {
                //        Image() // + mini circle to upload image
                Text(profile.username)
                Text(profile.role)
                Row {
                    Text(profile.rank)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(profile.score.toString())
                }
                Card() {
                    Text("Bio:")
                    Text(profile.bio)
                }

                /*
            if (!viewModel.testimonials.isEmpty()) {
                Text("Aportaciones:", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(viewModel.testimonials.size) { index ->
                        Text(viewModel.testimonials[index])
                    }
                }

            })
             */

            }
        } else {
            Text(
                "Error al obtener los datos del perfil:\n${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
