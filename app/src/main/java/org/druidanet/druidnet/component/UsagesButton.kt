package org.druidanet.druidnet.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import org.druidanet.druidnet.R

@Composable
fun ShowUsagesButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon( painterResource(R.drawable.usages),
            "Botón usos",
            modifier = Modifier.size(dimensionResource(R.dimen.section_buttom_img))
        )
        Text(text = "Ver usos")
    }
}