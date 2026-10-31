package com.bloom.app

import com.bloom.app.data.FaqItem
import com.bloom.app.data.GoalId
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.home.HomeViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HomeViewModel
    private val homeRepository = mockk<HomeRepository>()
    private val userRepository = mockk<UserRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // HomeViewModel only calls getFaqs() from homeRepository
        every { homeRepository.getFaqs() } returns listOf(
            FaqItem(1, "What is PCOS?", "PCOS is a hormonal disorder...")
        )
        every { userRepository.userName } returns flowOf("TestUser")
        every { userRepository.onboardingCompletedCache } returns MutableStateFlow(true)

        viewModel = HomeViewModel(homeRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has 5 default goals`() = runTest {
        advanceUntilIdle()
        assertEquals(5, viewModel.state.value.goals.size)
    }

    @Test
    fun `loadData sets userName from repository`() = runTest {
        advanceUntilIdle()
        assertEquals("TestUser", viewModel.state.value.userName)
    }

    @Test
    fun `loadData sets faqs from repository`() = runTest {
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.faqs.size)
        assertEquals("What is PCOS?", viewModel.state.value.faqs[0].question)
    }

    @Test
    fun `initial completedGoals is 0`() = runTest {
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.completedGoals)
    }

    @Test
    fun `tipVisible is true initially`() = runTest {
        advanceUntilIdle()
        assertTrue(viewModel.state.value.tipVisible)
    }

    @Test
    fun `incrementGoal increases MEALS current by 1`() = runTest {
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        assertEquals(1, meals.current)
    }

    @Test
    fun `incrementGoal does not exceed MEALS target of 3`() = runTest {
        advanceUntilIdle()
        repeat(10) { viewModel.incrementGoal(GoalId.MEALS) }
        val meals = viewModel.state.value.goals.first { it.id == GoalId.MEALS }
        assertEquals(3, meals.current)
    }

    @Test
    fun `incrementGoal sets a snackbar message`() = runTest {
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MOVEMENT)
        assertNotNull(viewModel.state.value.snackbarMessage)
    }

    @Test
    fun `completing all increments on MEALS shows completion message`() = runTest {
        advanceUntilIdle()
        repeat(3) { viewModel.incrementGoal(GoalId.MEALS) }
        val message = viewModel.state.value.snackbarMessage
        assertTrue(message?.contains("complete") == true || message?.contains("Meals") == true)
    }

    @Test
    fun `completedGoals increments when goal is finished`() = runTest {
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.completedGoals)
        repeat(3) { viewModel.incrementGoal(GoalId.MEALS) }   // target = 3
        assertEquals(1, viewModel.state.value.completedGoals)
    }

    @Test
    fun `hideTip sets tipVisible to false`() = runTest {
        advanceUntilIdle()
        viewModel.hideTip()
        assertTrue(!viewModel.state.value.tipVisible)
    }

    @Test
    fun `markSleepDone sets sleep current to target`() = runTest {
        advanceUntilIdle()
        viewModel.markSleepDone()
        val sleep = viewModel.state.value.goals.first { it.id == GoalId.SLEEP }
        assertEquals(sleep.target, sleep.current)
    }

    @Test
    fun `markSleepDone hides the tip card`() = runTest {
        advanceUntilIdle()
        viewModel.markSleepDone()
        assertTrue(!viewModel.state.value.tipVisible)
    }

    @Test
    fun `snackbarShown clears the snackbar message`() = runTest {
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        assertNotNull(viewModel.state.value.snackbarMessage)
        viewModel.snackbarShown()
        assertNull(viewModel.state.value.snackbarMessage)
    }

    @Test
    fun `overallProgress is 0 initially`() = runTest {
        advanceUntilIdle()
        assertEquals(0f, viewModel.state.value.overallProgress, 0.001f)
    }

    @Test
    fun `overallProgress increases after an increment`() = runTest {
        advanceUntilIdle()
        viewModel.incrementGoal(GoalId.MEALS)
        assertTrue(viewModel.state.value.overallProgress > 0f)
    }
}
