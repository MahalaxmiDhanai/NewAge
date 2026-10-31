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
            _state.update { it.copy(userName = name, faqs = faqs) }
        }
    }

    fun incrementGoal(goalId: GoalId) {
        _state.update { state ->
            val updatedGoals = state.goals.map { goal ->
                if (goal.id == goalId && goal.current < goal.target) {
                    goal.copy(current = goal.current + 1)
                } else goal
            }
            val updatedGoal = updatedGoals.first { it.id == goalId }
            val message = when {
                updatedGoal.current >= updatedGoal.target ->
                    "${updatedGoal.label} goal complete! 🎉"
                else ->
                    "${updatedGoal.label}: ${updatedGoal.current}/${updatedGoal.target} ${updatedGoal.unit}"
            }
            state.copy(goals = updatedGoals, snackbarMessage = message)
        }
    }

    fun markSleepDone() {
        _state.update { state ->
            val updatedGoals = state.goals.map { goal ->
                if (goal.id == GoalId.SLEEP) goal.copy(current = goal.target)
                else goal
            }
            state.copy(goals = updatedGoals, tipVisible = false)
        }
    }

    fun hideTip() {
        _state.update { it.copy(tipVisible = false) }
    }

    fun snackbarShown() {
        _state.update { it.copy(snackbarMessage = null) }
    }
}
