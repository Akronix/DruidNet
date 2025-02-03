package org.druidanet.druidnet.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.DruidNetApplication
import org.druidanet.druidnet.R
import org.druidanet.druidnet.Screen
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun AboutScreen (onNavigationButtonClick: (Screen) -> Unit, viewModel: DruidNetViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {

            AboutSectionHeader("Ajustes")

            AboutItem(
                action = { showDialog = true },
                stringResource(R.string.title_screen_language),
                imageResource = R.drawable.language
            )

            AboutSectionHeader("Acerca de " + stringResource(R.string.app_name))

            AboutItem(
                { onNavigationButtonClick( Screen.Bibliography ) },
                stringResource(R.string.title_screen_bibliography),
                imageResource = R.drawable.library_books,
                additionalText = null
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 50.dp)
            )

            AboutItem(
                {sendEmailAction(context)},
                "Contacta",
                additionalText = "¿Alguna sugerencia? ¿Quieres colaborar?",
                imageVector = Icons.Default.Email )
        }

        if (showDialog) {
            SwitchLanguageDialog(viewModel, {showDialog = false })
        }
    }
}

@Composable
fun SwitchLanguageDialog(viewModel: DruidNetViewModel, closeDialog: () -> Unit) {

    val radioOptions =
        arrayOf(LanguageEnum.CASTELLANO, LanguageEnum.CATALAN, LanguageEnum.GALLEGO, LanguageEnum.EUSKERA, LanguageEnum.LATIN)
    val initialLanguage = viewModel.getDisplayNameLanguage()

    val (selectedLanguage : LanguageEnum, onOptionSelected) = remember { mutableStateOf(initialLanguage) }

    // Show dialog
    AlertDialog(
        dismissButton = {
            TextButton(
                onClick = {
                    // Close dialog
                    closeDialog()
                }
            ) {
                Text("Cancelar",
                   style = MaterialTheme.typography.labelMedium
                )
            }
        },
        onDismissRequest = {
            // Close dialog
            closeDialog()
        },
        title = {
            Text("Idioma de los nombres",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column (Modifier.selectableGroup()
            ) {
            // Show Radio Button
                radioOptions.forEach { language ->
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (language == selectedLanguage),
                                onClick = { onOptionSelected(language) },
                                role = Role.RadioButton
                            )
                            .padding(start = 40.dp, bottom = 15.dp, top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        RadioButton(
                            selected = (language == selectedLanguage),
                            onClick = null,
                        )
                        Text(
                            language.displayWord,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // set selected language
                    viewModel.setLanguage(selectedLanguage)
                    closeDialog()
                }
            ) {
                Text(
                    stringResource(R.string.dialog_change_language),
                    style = MaterialTheme.typography.labelMedium)
            }
        },

    )

}

@Composable
fun AboutSectionHeader(sectionName: String) {
    Text(sectionName,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
        color = Color.Gray,
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.text_icon_setting_margin)
        )
    )
}

@Composable
fun AboutItem(
    action: () -> Unit,
    label: String,
    imageResource: Int? = null,
    imageVector: ImageVector? = null,
    additionalText: String? = null
) {
    Column(modifier = Modifier
                    .clickable(onClick = action)
                    .padding(
                        horizontal = dimensionResource(R.dimen.text_icon_setting_margin),
                        vertical = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
    ) {
        if (additionalText != null)
            Text(
                additionalText,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                modifier = Modifier.padding(bottom = 10.dp)
            )

        Row()
        {
            if (imageVector != null)
                Icon(
                    imageVector,
                    null,
                    modifier = Modifier.padding(
                        end = 20.dp
                    )
                )
            else if (imageResource != null)
                Icon(
                    painter = painterResource(imageResource),
                    null,
                    modifier = Modifier.padding(
                        end = 20.dp
                    )
                )

            Text(
                label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

fun sendEmailAction(context: Context) {

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("druidnetbeta@gmail.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, "DruidNetApp: ")
    }
    context.startActivity(intent)
}

@Composable
fun BibliographyScreen (modifier: Modifier = Modifier) {
    Box( modifier.verticalScroll(state = ScrollState(0)) ) {
        SelectionContainer {
            Markdown(
                """
                * Pardo de Santayana, Manuel, Ramón Morales, Laura Aceituno, y María Molina, eds. «Fase 1». En _Inventario español de los conocimientos tradicionales relativos a la biodiversidad_. Ministerio de Agricultura, Alimentación y Medio Ambiente, 2014.
                * Pardo de Santayana, Manuel, Ramón Morales, Javier Tardío, y María Molina, eds. «Fase 2 - Tomos 1, 2 y 3». En _Inventario español de los conocimientos tradicionales relativos a la biodiversidad_. Ministerio de Agricultura y Pesca, Alimentación y Medio Ambiente, 2018.
                * Bernard Bertrand. _Cocinar con plantas silvestres: Reconocer, recolectar, utilizar_. 2.a ed. La Fertilidad de la Tierra Ediciones, 2015.
                * César Lema Costas y otros/as. _Bienaventurada la «maleza» porque ella te salvará la cabeza_. Tórculo Artes Gráficas, 2016.
                * Francis Rose. _Clave de plantas silvestres_. Ediciones Omega, 1983.
                * Sociedad de Etnobiología. «Conect-e»,  https://conecte.es/.
                * Rakel Dawamoru Fernández Urdangarin. _Silvestre, comestible y creativo: Recetario para la soberanía alimentaria_. 3.a ed. Tórculo Comunicación Gráfica, 2013.
                * Luis Villar Pérez y otros. _Plantas medicinales del Pirineo aragonés y demás tierras oscenses_, 1987.
                * Pio Font Quer. _Plantas medicinales. Ediciones Península_, septiembre 2014. Edición orgininal: Editorial Labor, 1961.
                """.trimIndent(),
                typography = markdownTypography(text = MaterialTheme.typography.bodyMedium),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AboutPreview() {
//    DruidNetTheme(darkTheme = true) {
//        SwitchLanguageDialog(
//            { }
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun AboutPreview() {
//    DruidNetTheme(darkTheme = true) {
//        AboutScreen(
//            { },
//            null,
//            modifier = Modifier
//                .fillMaxSize()
//        )
//    }
//}