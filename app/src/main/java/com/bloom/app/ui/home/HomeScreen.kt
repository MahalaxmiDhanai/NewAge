package com.bloom.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.bloom.app.ui.theme.DarkText
import com.bloom.app.ui.theme.Dimens
import com.bloom.app.ui.theme.LightPink
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.SansFont
import com.bloom.app.ui.theme.SerifFont

// Figma gap between sections = 14dp
private val SectionGap = 14.dp

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

    // Handle system back button - if not on Dashboard, go back to Dashboard
    androidx.activity.compose.BackHandler(enabled = selectedNavIndex != 0) {
        selectedNavIndex = 0
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.snackbarShown()
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundCream,
        bottomBar = {
            Column {
                // Banner ad — collapses when no ad loaded
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
                state         = state,
                onGoalTap     = { viewModel.incrementGoal(it) },
                onRemindLater = { viewModel.hideTip() },
                onMarkAsDone  = { viewModel.markSleepDone() },
                modifier      = Modifier.padding(innerPadding)
            )
            else -> PlaceholderScreen(
                title    = when (selectedNavIndex) {
                    1 -> "Insights"
                    2 -> "Learn"
                    3 -> "Settings"
                    else -> ""
                },
                onBack   = { selectedNavIndex = 0 }, // Back to Dashboard
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
        // ── Header ──────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.ScreenPaddingH,
                        vertical   = Dimens.SpaceLg
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text          = "GOOD MORNING,",
                        fontFamily    = SansFont,
                        fontWeight    = FontWeight.Normal,
                        color         = MutedGrey,
                        fontSize      = 11.sp,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text       = state.userName,
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontStyle  = FontStyle.Italic,
                        color      = DarkText,
                        fontSize   = 22.sp
                    )
                }
                IconButton(
                    onClick  = {},
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LightPink)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint               = DarkText.copy(alpha = 0.55f),
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── Today's Goals card ──────────────────────────────────────────
        item {
            TodayGoalsCard(
                completedGoals  = state.completedGoals,
                totalGoals      = state.totalGoals,
                overallProgress = state.overallProgress,
                modifier        = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
            Spacer(modifier = Modifier.height(SectionGap))
        }

        // ── PCOS FAQ ────────────────────────────────────────────────────
        // Figma: "PCOS FAQ" label is SansFont bold (NOT italic serif)
        item {
            Text(
                text       = "PCOS FAQ",
                fontFamily = SansFont,
                fontWeight = FontWeight.Bold,
                color      = DarkText,
                fontSize   = 18.sp,
                modifier   = Modifier.padding(
                    horizontal = Dimens.ScreenPaddingH,
                    vertical   = 0.dp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            FaqPager(faqs = state.faqs)
            Spacer(modifier = Modifier.height(SectionGap))
        }

        // ── Quick Actions (Cycle + Symptom check-in) ────────────────────
        item {
            QuickActionCards(
                modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
            Spacer(modifier = Modifier.height(SectionGap))
        }

        // ── Goal Progress ───────────────────────────────────────────────
        item {
            SectionTitle(
                text     = "Goal Progress",
                modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Each goal is its own separate white card — matches Figma
        items(state.goals.size) { index ->
            val goal = state.goals[index]
            GoalRow(
                label            = goal.label,
                progress         = "${goal.current}/${goal.target} ${goal.unit}",
                ringColor        = goal.ringColor,
                progressFraction = goal.current.toFloat() / goal.target,
                icon             = goal.icon,
                onClick          = { onGoalTap(goal.id) },
                modifier         = Modifier.padding(
                    horizontal = Dimens.ScreenPaddingH,
                    vertical   = 3.dp
                )
            )
        }

        item { Spacer(modifier = Modifier.height(SectionGap)) }

        // ── Today's tip ─────────────────────────────────────────────────
        if (state.tipVisible) {
            item {
                SectionTitle(
                    text     = "Today's tip",
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingH)
                )
                Spacer(modifier = Modifier.height(8.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text       = title,
                        fontFamily = SerifFont,
                        fontStyle  = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        color      = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundCream
                )
            )
        },
        containerColor = BackgroundCream
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundCream),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "Coming soon",
                    fontFamily = SansFont,
                    fontSize   = 18.sp,
                    color      = MutedGrey
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    BloomTheme {
        DashboardContent(
            state = HomeUiState(
                userName   = "Sofia",
                goals      = HomeUiState().goals,
                faqs       = listOf(
                    com.bloom.app.data.FaqItem(1, "What exactly is PCOS?",
                        "PCOS stands for polycystic ovary syndrome...")
                ),
                tipVisible = true
            ),
            onGoalTap     = {},
            onRemindLater = {},
            onMarkAsDone  = {}
        )
    }
}
