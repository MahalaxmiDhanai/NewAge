package com.bloom.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloom.app.ads.AdConfig
import com.bloom.app.ads.BannerAdView
import com.bloom.app.ads.LogcatAdAnalytics
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.components.BloomBottomBar
import com.bloom.app.ui.components.GoalRow
import com.bloom.app.ui.components.SectionTitle
import com.bloom.app.ui.home.components.FaqPager
import com.bloom.app.ui.home.components.QuickActionCards
import com.bloom.app.ui.home.components.TodayGoalsCard
import com.bloom.app.ui.home.components.TodayTipCard
import com.bloom.app.ui.theme.BackgroundCream
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.CardShape
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.SerifFont
import com.bloom.app.ui.theme.White

@Composable
fun HomeScreen(
    homeRepository: HomeRepository,
    userRepository: UserRepository
) {
    val viewModel: HomeViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(homeRepository, userRepository) as T
            }
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }

    // Show snackbar
    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.snackbarShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundCream,
        bottomBar = {
            Column {
                // Banner ad slot — above nav bar, collapses if no ad
                BannerAdView(
                    adUnitId  = AdConfig.BANNER_AD_UNIT_ID,
                    analytics = LogcatAdAnalytics(),
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.BannerSlotPaddingV)
                )
                BloomBottomBar(
                    selectedIndex  = selectedNavIndex,
                    onItemSelected = { selectedNavIndex = it }
                )
            }
        }
    ) { innerPadding ->
        when (selectedNavIndex) {
            0 -> DashboardContent(
                state    = state,
                onGoalTap = { viewModel.incrementGoal(it) },
                onRemindLater = { viewModel.hideTip() },
                onMarkAsDone  = { viewModel.markSleepDone() },
                modifier = Modifier.padding(innerPadding)
            )
            else -> PlaceholderScreen(
                title    = when (selectedNavIndex) {
                    1 -> "Insights"
                    2 -> "Learn"
                    3 -> "Settings"
                    else -> ""
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun DashboardContent(
    state: HomeUiState,
    onGoalTap: (com.bloom.app.data.GoalId) -> Unit,
    onRemindLater: () -> Unit,
    onMarkAsDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.ScreenPaddingH,
                        vertical   = Dimens.SpaceLg
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "GOOD MORNING,",
                        fontFamily = SansFont,
                        fontWeight = FontWeight.Normal,
                        color      = MutedGrey,
                        fontSize   = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text       = state.userName,
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontStyle  = FontStyle.Italic,
                        color      = DarkText,
                        fontSize   = 24.sp
                    )
                }
                IconButton(
                    onClick  = {},
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LightPink)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint               = DarkText.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // ── Today's Goals card ───────────────────────────────────────────────
        item {
            TodayGoalsCard(
                completedGoals  = state.completedGoals,
                totalGoals      = state.totalGoals,
                overallProgress = state.overallProgress,
                modifier        = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
        }

        item { Spacer(modifier = Modifier.height(Dimens.SpaceXl)) }

        // ── PCOS FAQ ─────────────────────────────────────────────────────────
        item {
            SectionTitle(
                text     = "PCOS FAQ",
                modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceMd))
            FaqPager(faqs = state.faqs)
        }

        item { Spacer(modifier = Modifier.height(Dimens.SpaceXl)) }

        // ── Quick actions ────────────────────────────────────────────────────
        item {
            QuickActionCards(
                modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
        }

        item { Spacer(modifier = Modifier.height(Dimens.SpaceXl)) }

        // ── Goal Progress ────────────────────────────────────────────────────
        item {
            SectionTitle(
                text     = "Goal Progress",
                modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceMd))
        }

        item {
            Card(
                shape     = CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors    = CardDefaults.cardColors(containerColor = White),
                modifier  = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            ) {
                state.goals.forEachIndexed { index, goal ->
                    GoalRow(
                        label            = goal.label,
                        progress         = "${goal.current}/${goal.target} ${goal.unit}",
                        ringColor        = goal.ringColor,
                        progressFraction = goal.current.toFloat() / goal.target,
                        onClick          = { onGoalTap(goal.id) },
                        showDivider      = index < state.goals.lastIndex
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(Dimens.SpaceXl)) }

        // ── Today's tip ──────────────────────────────────────────────────────
        if (state.tipVisible) {
            item {
                SectionTitle(
                    text     = "Today's tip",
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceMd))
                TodayTipCard(
                    onRemindLater = onRemindLater,
                    onMarkAsDone  = onMarkAsDone,
                    modifier      = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(Dimens.SpaceLg)) }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier        = modifier
            .fillMaxSize()
            .background(BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SectionTitle(title)
            Spacer(modifier = Modifier.height(Dimens.SpaceMd))
            Text(
                text       = "Coming soon",
                fontFamily = SansFont,
                color      = MutedGrey
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    BloomTheme {
        DashboardContent(
            state         = HomeUiState(
                userName   = "Sofia",
                goals      = HomeUiState().goals,
                faqs       = listOf(
                    com.bloom.app.data.FaqItem(1, "What exactly is PCOS?", "PCOS stands for polycystic ovary syndrome...")
                ),
                tipVisible = true
            ),
            onGoalTap     = {},
            onRemindLater = {},
            onMarkAsDone  = {}
        )
    }
}
