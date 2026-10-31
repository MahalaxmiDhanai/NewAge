package com.bloom.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.QuickActionCardBg
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

@Composable
fun QuickActionCards(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        QuickActionRow(
            iconEmoji   = "🩸",
            title       = "Cycle",
            subtitle    = "Log your last period date",
            onClick     = {}
        )
        QuickActionRow(
            iconEmoji   = "😊",
            title       = "Symptom check-in",
            subtitle    = "Bloating, skin, mood, sleep quality",
            onClick     = {}
        )
    }
}

@Composable
private fun QuickActionRow(
    iconEmoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape    = CardShape,
        colors   = CardDefaults.cardColors(containerColor = QuickActionCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // White icon chip
            Box(
                modifier        = Modifier
                    .size(40.dp)
                    .background(White, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(Dimens.SpaceLg))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText,
                    fontSize   = 15.sp
                )
                Text(
                    text       = subtitle,
                    fontFamily = SansFont,
                    color      = MutedGrey,
                    fontSize   = 13.sp
                )
            }
            Icon(
                imageVector        = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint               = MutedGrey
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickActionCardsPreview() {
    BloomTheme {
        QuickActionCards(modifier = Modifier.padding(Dimens.SpaceLg))
    }
}
