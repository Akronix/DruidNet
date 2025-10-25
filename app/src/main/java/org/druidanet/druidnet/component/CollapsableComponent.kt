package org.druidanet.druidnet.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnet.R

@Composable
fun CollapsableSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
                    .padding(start = 10.dp)
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painterResource( if (expanded) R.drawable.unfold_less_20 else R.drawable.unfold_more_20 ),
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 7.dp, top = 10.dp)) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun CollapsableSectionPreview() {
    CollapsableSection(
        title = "Preview Title"
    ) {
        Text(text = "This is the content of the collapsable section.")
    }
}
