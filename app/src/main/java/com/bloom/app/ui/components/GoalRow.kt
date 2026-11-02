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
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

/**
 * Goal card layout from Figma: ring icon (left) + label/progress + chevron (right).
 * Each goal gets its own white card with subtle elevation.
 */
@Composable
fun GoalRow(
    label: String,
    progress: String,
    ringColor: Color,
    progressFraction: Float,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true   // kept for API compat
) {
    Card(
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier  = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ring with icon inside
            RingIcon(
                color    = ringColor,
                progress = progressFraction,
                icon     = icon,
                size     = 42.dp,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.width(Dimens.SpaceLg))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = label,
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText,
                    style      = MaterialTheme.typography.bodyMedium
                        .copy(fontWeight = FontWeight.SemiBold)
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
                tint               = MutedGrey,
                modifier           = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun GoalRowPreview() {
    BloomTheme {
        GoalRow(
            label            = "Meals",
            progress         = "0/3 meals",
            ringColor        = PrimaryBrickRed,
            progressFraction = 0f,
            icon             = Icons.Outlined.LocalDining,
            onClick          = {},
            modifier         = Modifier.padding(horizontal = Dimens.SpaceLg, vertical = 3.dp)
        )
    }
}
