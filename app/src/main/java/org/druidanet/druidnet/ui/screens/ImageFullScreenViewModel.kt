package org.druidanet.druidnet.ui.screens

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.druidanet.druidnet.data.plant.OnlineImagesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.navigation.FullScreenDestination
import org.druidanet.druidnet.utils.findImageLocalPath
import javax.inject.Inject

data class FullScreenUIState(
    val images: List<String> = emptyList(),
    val attributions: List<String> = emptyList(),
    val initialIndex: Int = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ImageFullScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val onlineImagesRepository: OnlineImagesRepository,
    private val plantsRepository: PlantsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val plantLatinName: String = checkNotNull(savedStateHandle[FullScreenDestination.plantArg])
    private val initialIndex: Int = savedStateHandle.get<String>(FullScreenDestination.indexArg)?.toInt() ?: 0

    val uiState: StateFlow<FullScreenUIState> = combine(
        plantsRepository.getPlant(plantLatinName, LanguageEnum.LATIN),
        onlineImagesRepository.getOnlineImages(plantLatinName)
    ) { plant, onlineImageData ->
        val localUrl = findImageLocalPath(plant.imagePath, context)
        val allUrls = listOf(localUrl) + onlineImageData.urls
        val allAttributions = listOf("(c) DruidNet (CC BY-NC-SA 4.0)") + onlineImageData.attributions

        FullScreenUIState(
            images = allUrls,
            attributions = allAttributions,
            initialIndex = initialIndex,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FullScreenUIState()
    )
}
