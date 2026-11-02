package com.bloom.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.app.data.FaqItem
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.White

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FaqPager(
    faqs: List<FaqItem>,
    modifier: Modifier = Modifier
) {
    if (faqs.isEmpty()) return

    val pagerState = rememberPagerState { faqs.size }

    Column(modifier = modifier) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            FaqCard(
                faq      = faqs[page],
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.ScreenPaddingH)
            )
        }

        // Pager dots indicator
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpaceSm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(faqs.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (isSelected) 20.dp else 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isSelected) PrimaryBrickRed
                            else MutedGrey.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
private fun FaqCard(
    faq: FaqItem,
    modifier: Modifier = Modifier
) {
    Card(
        shape     = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        modifier  = modifier
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = faq.question,
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText,
                    fontSize   = 14.sp
                )
                Text(
                    text       = faq.answer,
                    fontFamily = SansFont,
                    color      = MutedGrey,
                    fontSize   = 13.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector        = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint               = MutedGrey,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun FaqPagerPreview() {
    BloomTheme {
        FaqPager(
            faqs = listOf(
                FaqItem(1, "What exactly is PCOS?", "PCOS stands for polycystic ovary syndrome..."),
                FaqItem(2, "What causes PCOS?", "The exact cause isn't known...")
            )
        )
    }
}
