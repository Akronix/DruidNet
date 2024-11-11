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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
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
                imageResource = R.drawable.library_books)

            AboutItem(
                {sendEmailAction(context)},
                "Contacto",
                imageVector = Icons.Default.Email )
        }
    }
}

@Composable
fun AboutItem(action: () -> Unit,
              label: String,
              imageResource: Int? = null,
              imageVector:  ImageVector? = null ) {
    Row (
        modifier = Modifier
            .clickable(onClick = action)
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    )
    {
        if (imageVector != null)
            Icon(
                imageVector,
                null,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.text_icon_setting_margin)
                )
            )
        else if (imageResource != null)
            Icon(
                painter = painterResource(imageResource),
                null,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.text_icon_setting_margin)
                )
            )

        Text(label,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp))
    }
}

fun sendEmailAction(context: Context) {

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("druidnetbeta@gmail.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, "DruidNetApp: ")
    }
    startActivity(context, intent, null)
}

@Composable
fun ReferencesScreen ( modifier: Modifier = Modifier) {
    Box( modifier ) {
        Text ( """
            * Pardo de Santayana, Manuel, Ramón Morales, Laura Aceituno, y María Molina, eds. «Fase 1». En Inventario español de los conocimientos tradicionales relativos a la biodiversidad. Ministerio de Agricultura, Alimentación y Medio Ambiente, 2014.
            * Pardo de Santayana, Manuel, Ramón Morales, Javier Tardío, y María Molina, eds. «Fase 2 - Tomos 1, 2 y 3». En Inventario español de los conocimientos tradicionales relativos a la biodiversidad. Ministerio de Agricultura y Pesca, Alimentación y Medio Ambiente, 2018.
            * Bertrand, Bernard. Cocinar con plantas silvestres: Reconocer, recolectar, utilizar. 2.a ed. La Fertilidad de la Tierra Ediciones, 2015.
            * Costas, César Lema y otros/as. Bienaventurada la «maleza» porque ella te salvará la cabeza. Tórculo Artes Gráficas, 2016.
            * Rose, Francis. Clave de plantas silvestres. Ediciones Omega, 1983.
            * Sociedad de Etnobiología. «Conect-e»,  https://conecte.es/.
            * Urdangarin, Rakel Dawamoru Fernández. Silvestre, comestible y creativo: Recetario para la soberanía alimentaria. 3.a ed. Tórculo Comunicación Gráfica, 2013.
            """
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