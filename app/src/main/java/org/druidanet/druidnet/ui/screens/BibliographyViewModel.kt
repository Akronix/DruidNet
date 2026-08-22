package org.druidanet.druidnet.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.druidanet.druidnet.data.bibliography.BibliographyDAO
import org.druidanet.druidnet.navigation.BibliographyDestination
import javax.inject.Inject

@HiltViewModel
class BibliographyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bibliographyDAO: BibliographyDAO
) : ViewModel() {

    private val plantId: Int = checkNotNull(savedStateHandle[BibliographyDestination.plantIdArg])

    val bibliographyMarkdown: StateFlow<String> = (
        if (plantId != 0) {
            bibliographyDAO.getBibliographyForPlant(plantId)
        } else {
            bibliographyDAO.getAllBibliographyEntries()
        }
    ).onEach { bibliography ->
        Log.i("BIBIO", "Plant ID: $plantId - Items found: ${bibliography.size}")
        bibliography.forEach { item ->
            Log.d("BIBIO", "Entry: ${item.title}")
        }
    }.map { bibliography ->
        if (bibliography.isNotEmpty()) {
            bibliography.map { "* " + it.toMarkdownString() }
                .reduce { acc, ref -> "$acc\n$ref" }
        } else {
            ""
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )
}
