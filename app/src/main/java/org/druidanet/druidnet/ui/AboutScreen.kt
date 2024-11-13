package org.druidanet.druidnet.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.R
import org.druidanet.druidnet.Screen
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun AboutScreen (onNavigationButtonClick: (Screen) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {

            AboutItem(
                { onNavigationButtonClick( Screen.References ) },
                stringResource(R.string.references_about_item_label),
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
    }
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
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
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
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
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
fun ReferencesScreen ( modifier: Modifier = Modifier) {
    Box( modifier ) {
        Markdown(
            """
            * Pardo de Santayana, Manuel, Ramón Morales, Laura Aceituno, y María Molina, eds. «Fase 1». En _Inventario español de los conocimientos tradicionales relativos a la biodiversidad_. Ministerio de Agricultura, Alimentación y Medio Ambiente, 2014.
            * Pardo de Santayana, Manuel, Ramón Morales, Javier Tardío, y María Molina, eds. «Fase 2 - Tomos 1, 2 y 3». En _Inventario español de los conocimientos tradicionales relativos a la biodiversidad_. Ministerio de Agricultura y Pesca, Alimentación y Medio Ambiente, 2018.
            * Bertrand, Bernard. _Cocinar con plantas silvestres: Reconocer, recolectar, utilizar_. 2.a ed. La Fertilidad de la Tierra Ediciones, 2015.
            * Costas, César Lema y otros/as. _Bienaventurada la «maleza» porque ella te salvará la cabeza_. Tórculo Artes Gráficas, 2016.
            * Rose, Francis. _Clave de plantas silvestres_. Ediciones Omega, 1983.
            * Sociedad de Etnobiología. «Conect-e»,  https://conecte.es/.
            * Urdangarin, Rakel Dawamoru Fernández. _Silvestre, comestible y creativo: Recetario para la soberanía alimentaria_. 3.a ed. Tórculo Comunicación Gráfica, 2013.
            """.trimIndent(),
            typography = markdownTypography(text = MaterialTheme.typography.bodyMedium),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutPreview() {
    DruidNetTheme(darkTheme = true) {
        AboutScreen(
            { },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}