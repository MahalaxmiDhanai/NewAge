package com.bloom.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.app.ui.components.FlowerLogo
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.GoalsCardGradientEnd
import com.bloom.app.ui.theme.GoalsCardGradientStart
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.SerifFont
import com.bloom.app.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayGoalsCard(
    completedGoals: Int,
    totalGoals: Int,
    overallProgress: Float,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
    val dateString = today.format(formatter)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GoalsCardGradientStart, GoalsCardGradientEnd)
                )
            )
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimens.SpaceLg)
            ) {
                Text(
                    text       = "Today's Goals",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontStyle  = FontStyle.Italic,
                    color      = PrimaryBrickRed,
                    fontSize   = 18.sp
                )
                Text(
                    text       = dateString,
                    fontFamily = SansFont,
                    color      = MutedGrey,
                    fontSize   = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "Let's get blooming",
                    fontFamily = SansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 13.sp,
                    color      = PrimaryBrickRed.copy(alpha = 0.8f)
                )
                Text(
                    text       = "$completedGoals of $totalGoals complete",
                    fontFamily = SansFont,
                    color      = MutedGrey,
                    fontSize   = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress    = { overallProgress },
                    modifier    = Modifier
                        .fillMaxWidth(0.8f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color       = PrimaryBrickRed,
                    trackColor  = PrimaryBrickRed.copy(alpha = 0.2f),
                    strokeCap   = StrokeCap.Round
                )
            }

            // Right side: faded flower + "Start here" label
            Box(
                modifier        = Modifier.padding(end = Dimens.SpaceLg),
                contentAlignment = Alignment.Center
            ) {
                FlowerLogo(
                    size     = 80.dp,
                    showGlow = false,
                    modifier = Modifier.alpha(0.18f)
                )
                Text(
                    text       = "Start here",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontStyle  = FontStyle.Italic,
                    color      = PrimaryBrickRed,
                    fontSize   = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun TodayGoalsCardPreview() {
    BloomTheme {
        TodayGoalsCard(
            completedGoals  = 2,
            totalGoals      = 5,
            overallProgress = 0.4f,
            modifier        = Modifier.padding(Dimens.SpaceLg)
        )
    }
}
