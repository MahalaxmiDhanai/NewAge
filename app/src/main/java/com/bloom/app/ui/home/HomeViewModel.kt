package com.bloom.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.data.GoalId
import com.bloom.app.data.GoalItem
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val name = userRepository.userName.first()
            val faqs = homeRepository.getFaqs()
            
            // Load saved progress and check if it's today
            val savedDate = userRepository.lastProgressDate.first()
            val today = LocalDate.now().toString()
            
            val goals = if (savedDate == today) {
                // Same day: restore saved progress
                val defaultGoals = homeRepository.getDefaultGoals()
                defaultGoals.map { goal ->
                    val savedCurrent = when (goal.id) {
                        GoalId.MEALS      -> userRepository.goalMeals.first()
                        GoalId.MOVEMENT   -> userRepository.goalMovement.first()
                        GoalId.WATER      -> userRepository.goalWater.first()
                        GoalId.RELAXATION -> userRepository.goalRelaxation.first()
                        GoalId.SLEEP      -> userRepository.goalSleep.first()
                    }
                    goal.copy(current = savedCurrent)
                }
            } else {
                // New day: reset progress
                userRepository.resetDailyProgress(today)
                homeRepository.getDefaultGoals()
            }
            
            val tipVisible = if (savedDate == today) {
                userRepository.tipVisible.first()
            } else {
                true
            }
            
            _state.update { it.copy(userName = name, faqs = faqs, goals = goals, tipVisible = tipVisible) }
        }
    }

    fun incrementGoal(goalId: GoalId) {
        _state.update { state ->
            val updatedGoals = state.goals.map { goal ->
                if (goal.id == goalId && goal.current < goal.target) {
                    goal.copy(current = goal.current + 1)
                } else goal
            }
            state.copy(goals = updatedGoals)
        }
        
        // Save progress in background
        saveProgress()
    }

    fun markSleepDone() {
        _state.update { state ->
            val updatedGoals = state.goals.map { goal ->
                if (goal.id == GoalId.SLEEP) goal.copy(current = goal.target)
                else goal
            }
            state.copy(goals = updatedGoals, tipVisible = false)
        }
        
        // Save progress in background
        saveProgress()
    }

    fun hideTip() {
        _state.update { it.copy(tipVisible = false) }
        
        // Save progress in background
        saveProgress()
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val currentState = _state.value
            val today = LocalDate.now().toString()
            
            userRepository.saveGoalProgress(
                date = today,
                meals = currentState.goals.first { it.id == GoalId.MEALS }.current,
                movement = currentState.goals.first { it.id == GoalId.MOVEMENT }.current,
                water = currentState.goals.first { it.id == GoalId.WATER }.current,
                relaxation = currentState.goals.first { it.id == GoalId.RELAXATION }.current,
                sleep = currentState.goals.first { it.id == GoalId.SLEEP }.current,
                tipVisible = currentState.tipVisible
            )
        }
    }
}
