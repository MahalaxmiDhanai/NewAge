package com.bloom.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont

@Composable
fun GoalRow(
    label: String,
    progress: String,
    ringColor: Color,
    progressFraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RingIcon(color = ringColor, progress = progressFraction)
            Spacer(modifier = Modifier.width(Dimens.SpaceLg))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = label,
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText
                )
                Text(
                    text  = progress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGrey
                )
            }
            Icon(
                imageVector        = Icons.Filled.ChevronRight,
                contentDescription = "Open $label",
                tint               = MutedGrey
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(horizontal = Dimens.SpaceLg),
                thickness = 0.5.dp,
                color     = Color(0xFFF0E8E6)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalRowPreview() {
    BloomTheme {
        GoalRow(
            label            = "Movement",
            progress         = "0/30 min",
            ringColor        = PrimaryBrickRed,
            progressFraction = 0.4f,
            onClick          = {}
        )
    }
}
