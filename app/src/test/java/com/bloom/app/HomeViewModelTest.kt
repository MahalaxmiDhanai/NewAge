package com.bloom.app

import androidx.compose.ui.graphics.Color
import com.bloom.app.data.FaqItem
import com.bloom.app.data.GoalId
import com.bloom.app.data.GoalItem
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HomeViewModel
    private val homeRepository = mockk<HomeRepository>()
    private val userRepository = mockk<UserRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val mockGoals = listOf(
            GoalItem(GoalId.MEALS, "Meals", "meals", 0, 3, Color.Red, mockk()),
            GoalItem(GoalId.MOVEMENT, "Movement", "min", 0, 30, Color.Green, mockk()),
            GoalItem(GoalId.WATER, "Water", "glasses", 0, 8, Color.Blue, mockk()),
            GoalItem(GoalId.RELAXATION, "Relaxation", "min", 0, 15, Color.Magenta, mockk()),
            GoalItem(GoalId.SLEEP, "Sleep", "hrs", 0, 8, Color.Cyan, mockk())
        )

        every { homeRepository.getFaqs() } returns listOf(
            FaqItem(1, "What is PCOS?", "PCOS is a hormonal disorder...")
        )
        every { homeRepository.getDefaultGoals() } returns mockGoals
        every { userRepository.userName } returns flowOf("TestUser")
        every { userRepository.onboardingCompletedCache } returns MutableStateFlow(true)
        
        // Default: no saved progress
        every { userRepository.lastProgressDate } returns flowOf(null)
        every { userRepository.goalMeals } returns flowOf(0)
        every { userRepository.goalMovement } returns flowOf(0)
        every { userRepository.goalWater } returns flowOf(0)
        every { userRepository.goalRelaxation } returns flowOf(0)
        every { userRepository.goalSleep } returns flowOf(0)
        every { userRepository.tipVisible } returns flowOf(true)
        
        coEvery { userRepository.saveGoalProgress(any(), any(), any(), any(), any(), any(), any()) } returns Unit
        coEvery { userRepository.resetDailyProgress(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has 5 default goals`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals(5, viewModel.state.value.goals.size)
    }

    @Test
    fun `loadData sets userName from repository`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals("TestUser", viewModel.state.value.userName)
    }

    @Test
    fun `loadData sets faqs from repository`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.faqs.size)
        assertEquals("What is PCOS?", viewModel.state.value.faqs[0].question)
    }

    @Test
    fun `initial completedGoals is 0`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.completedGoals)
    }

    @Test
    fun `tipVisible is true initially`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.tipVisible)
    }

    @Test
    fun `incrementGoal increases MEALS current by 1`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        advanceUntilIdle()
        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        assertEquals(1, meals.current)
    }

    @Test
    fun `incrementGoal does not exceed MEALS target of 3`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        repeat(10) { viewModel.incrementGoal(GoalId.MEALS) }
        advanceUntilIdle()
        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        assertEquals(3, meals.current)
    }

    @Test
    fun `incrementGoal saves progress to repository`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        advanceUntilIdle()
        coVerify { userRepository.saveGoalProgress(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `completedGoals increments when goal is finished`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.completedGoals)
        repeat(3) { viewModel.incrementGoal(GoalId.MEALS) }
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.completedGoals)
    }

    @Test
    fun `hideTip sets tipVisible to false`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.hideTip()
        assertTrue(!viewModel.state.value.tipVisible)
    }

    @Test
    fun `markSleepDone sets sleep current to target`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.markSleepDone()
        val sleep = viewModel.state.value.goals.first { it.id == GoalId.SLEEP }
        assertEquals(sleep.target, sleep.current)
    }

    @Test
    fun `markSleepDone hides the tip card`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.markSleepDone()
        assertTrue(!viewModel.state.value.tipVisible)
    }

    @Test
    fun `overallProgress is 0 initially`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        assertEquals(0f, viewModel.state.value.overallProgress, 0.001f)
    }

    @Test
    fun `overallProgress increases after an increment`() = runTest {
        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.overallProgress > 0f)
    }

    // ── Daily reset tests ────────────────────────────────────────────────────

    @Test
    fun `same day keeps saved progress`() = runTest {
        val today = LocalDate.now().toString()
        every { userRepository.lastProgressDate } returns flowOf(today)
        every { userRepository.goalMeals } returns flowOf(2)
        every { userRepository.goalMovement } returns flowOf(15)
        every { userRepository.tipVisible } returns flowOf(false)

        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()

        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        val movement = viewModel.state.value.goals.first { it.id == GoalId.MOVEMENT }
        
        assertEquals(2, meals.current)
        assertEquals(15, movement.current)
        assertEquals(false, viewModel.state.value.tipVisible)
        
        coVerify(exactly = 0) { userRepository.resetDailyProgress(any()) }
    }

    @Test
    fun `different day resets progress`() = runTest {
        val yesterday = LocalDate.now().minusDays(1).toString()
        every { userRepository.lastProgressDate } returns flowOf(yesterday)
        every { userRepository.goalMeals } returns flowOf(3)
        every { userRepository.goalMovement } returns flowOf(30)

        viewModel = HomeViewModel(homeRepository, userRepository)
        advanceUntilIdle()

        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        val movement = viewModel.state.value.goals.first { it.id == GoalId.MOVEMENT }
        
        assertEquals(0, meals.current)
        assertEquals(0, movement.current)
        assertEquals(true, viewModel.state.value.tipVisible)
        
        coVerify { userRepository.resetDailyProgress(LocalDate.now().toString()) }
    }
}
