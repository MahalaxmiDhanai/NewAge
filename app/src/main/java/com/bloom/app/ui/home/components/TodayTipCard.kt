package com.bloom.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.GoalGreen
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PillShape
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.TipCardBg
import com.bloom.app.ui.theme.TipLabelText
import com.bloom.app.ui.theme.TipSecondaryButton
import com.bloom.app.ui.theme.White

@Composable
fun TodayTipCard(
    onRemindLater: () -> Unit,
    onMarkAsDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TipCardBg, shape = CardShape)
            .padding(Dimens.SpaceLg)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Moon icon chip — beige/tan background, crescent moon icon, matching Figma
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = androidx.compose.ui.graphics.Color(0xFFD9C9A8),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Bedtime,
                    contentDescription = null,
                    tint               = TipLabelText,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(Dimens.SpaceMd))
            Text(
                text       = "LOG LAST NIGHT'S SLEEP",
                fontFamily = SansFont,
                fontWeight = FontWeight.Bold,
                color      = TipLabelText,
                fontSize   = 11.sp,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceMd))

        Text(
            text       = "You haven't logged sleep yet. Consistent, close-to-8-hr nights can help keep your hormones more balanced — avoiding phones and screens before bed makes it easier to fall asleep.",
            fontFamily = SansFont,
            color      = DarkText.copy(alpha = 0.85f),
            fontSize   = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceLg))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)
        ) {
            // "Remind me later" — filled beige
            Button(
                onClick = onRemindLater,
                shape   = PillShape,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = TipSecondaryButton,
                    contentColor   = DarkText
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text       = "Remind me later",
                    fontFamily = SansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 13.sp
                )
            }

            // "Mark as done" — solid green
            Button(
                onClick = onMarkAsDone,
                shape   = PillShape,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = GoalGreen,
                    contentColor   = White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Check,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text       = "Mark as done",
                    fontFamily = SansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun TodayTipCardPreview() {
    BloomTheme {
        TodayTipCard(
            onRemindLater = {},
            onMarkAsDone  = {},
            modifier      = Modifier.padding(Dimens.SpaceLg)
        )
    }
}
