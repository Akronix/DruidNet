package org.druidanet.druidnet.ui.screens

import NavigationDestination
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.BibliographyDestination
import org.druidanet.druidnet.CreditsDestination
import org.druidanet.druidnet.DruidNetAppBar
import org.druidanet.druidnet.R
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.DEFAULT_CREDITS_TXT

@Composable
fun AboutLayout (navigateBack: () -> Unit,
                    topBarTitle: String,
                    modifier: Modifier = Modifier,
                    content: @Composable () -> Unit) {
    Scaffold(
        topBar = DruidNetAppBar(
            navigateUp = navigateBack,
            topBarTitle = topBarTitle,
        ),
    ) {
            innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding)
        ) {
            content()
            }
        }
}

@Composable
fun AboutScreen (
    navigateBack: () -> Unit,
    onNavigationButtonClick: (NavigationDestination) -> Unit,
     viewModel: DruidNetViewModel,
     modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    AboutLayout(
            navigateBack = navigateBack,
            topBarTitle = stringResource(R.string.title_screen_about),
            modifier = modifier)
        {
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
                    { onNavigationButtonClick(BibliographyDestination) },
                    stringResource(R.string.title_screen_bibliography),
                    imageResource = R.drawable.library_books,
                    additionalText = null
                )

                AboutItem(
                    { openURIAction(context, "https://druidnet.es/preguntas-frecuentes/") },
                    stringResource(R.string.about_screen_faqs),
                    imageResource = R.drawable.help,
                    additionalText = null
                )

                AboutItem(
                    { onNavigationButtonClick(CreditsDestination) },
                    stringResource(R.string.title_screen_credits),
                    imageVector = Icons.Default.Star,
                    additionalText = null
                )

                AboutItem(
                    {
                        openURIAction(
                            context,
                            "https://opencollective.com/druidnet#category-CONTRIBUTE"
                        )
                    },
                    stringResource(R.string.about_screen_donate),
                    imageResource = R.drawable.donate,
                    additionalText = null
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 50.dp)
                )

                AboutItem(
                    { sendEmailAction(context) },
                    "Contacta",
                    additionalText = "¿Alguna sugerencia? ¿Quieres colaborar?",
                    imageVector = Icons.Default.Email
                )
            }

            if (showDialog) {
                SwitchLanguageDialog(viewModel, { showDialog = false })
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
                            language.displayLanguage,
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

        Row(verticalAlignment = Alignment.CenterVertically,)
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

fun openURIAction(context: Context, uri: String) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(uri)
    }
    context.startActivity(intent)
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
fun CreditsScreen (
    navigateBack: () -> Unit,
    creditsText: String,
    modifier: Modifier = Modifier) {
    AboutLayout(
        navigateBack = navigateBack,
        topBarTitle = stringResource(R.string.title_screen_credits),
        modifier = modifier
    )
    {
        Column(
            modifier = modifier.fillMaxHeight() // Take up the full available height
                .verticalScroll(state = ScrollState(0)),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Markdown(
                creditsText,
                typography = markdownTypography(
                    h1 = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    paragraph = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp)
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
            Markdown(
                """
                <br/>
                ---
                <br/>
                El contenido textual está redactado a partir de varias fuentes y tiene licencia [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).
                
                El contenido gráfico está realizado a partir de imágenes propias o de imágenes de dominio público y tiene licencia [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).
                
                El código es software libre y está disponible en: https://github.com/Akronix/DruidNet
                
                Puedes atribuir el contenido con la siguiente línea:
                [Nombre contenido](plant_sheet/Sambucus_nigra) por [Nombre de autor]. «DruidNet» - 2025. y enlace a la licencia [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)."
        """.trimIndent(),
                typography = markdownTypography(
                    paragraph = MaterialTheme.typography.bodySmall,
                    link = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    )
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }
}

@Composable
fun BibliographyScreen (
    navigateBack: () -> Unit,
    bibliographyStr: String,
    modifier: Modifier = Modifier) {

    AboutLayout(
        navigateBack = navigateBack,
        topBarTitle = stringResource(R.string.title_screen_bibliography),
        modifier = modifier.verticalScroll(state = ScrollState(0))
    )
    {

        SelectionContainer {

            Markdown(
                bibliographyStr.trimIndent(),
                typography = markdownTypography(
                    text = MaterialTheme.typography.bodyMedium,
                    link = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.Underline
                    )
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun BiblioPreview() {
    DruidNetTheme(darkTheme = false) {
        BibliographyScreen(
            { },
            BibliographyEntity(
                1,
                "incollection",
                "INVENTARIO ESPAÑOL DE LOS CONOCIMIENTOS TRADICIONALES RELATIVOS A LA BIODIVERSIDAD",
                "Pardo de Santayana, Manuel, Ramón Morales, Laura Aceituno, y María Molina (editores)",
                "Ministerio de Agricultura, Alimentación y Medio Ambiente",
                "2014",
                "978-84-491-1401-4",
                null,
                "https://www.miteco.gob.es/es/biodiversidad/temas/inventarios-nacionales/inventario-espanol-de-los-conocimientos-tradicionales/inventario_esp_conocimientos_tradicionales.html",
                "Fase 1"
            ).toMarkdownString()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreditsPreview() {
    DruidNetTheme(darkTheme = false) {
        CreditsScreen(
            {},
            DEFAULT_CREDITS_TXT
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AboutPreview() {
//    DruidNetTheme(darkTheme = true) {
//        AboutScreen(
//            { },
//            { },
//            null,
//            Modifier
//        )
//    }
//}