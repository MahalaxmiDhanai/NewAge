package com.bloom.app.ui.home

import com.bloom.app.data.FaqItem
import com.bloom.app.data.GoalItem
import com.bloom.app.data.GoalId
import com.bloom.app.ui.theme.GoalBlue
import com.bloom.app.ui.theme.GoalGreen
import com.bloom.app.ui.theme.GoalIndigo
import com.bloom.app.ui.theme.GoalPurple
import com.bloom.app.ui.theme.PrimaryBrickRed

data class HomeUiState(
    val userName: String = "Sofia",
    val goals: List<GoalItem> = defaultGoals(),
    val faqs: List<FaqItem> = emptyList(),
    val tipVisible: Boolean = true,
    val snackbarMessage: String? = null
) {
    val completedGoals: Int get() = goals.count { it.current >= it.target }
    val totalGoals: Int    get() = goals.size
    val overallProgress: Float
        get() = if (goals.isEmpty()) 0f
                else goals.sumOf { (it.current.toDouble() / it.target) }.toFloat() / goals.size
}

private fun defaultGoals() = listOf(
    GoalItem(GoalId.MEALS,      "Meals",      "meals", 0, 3,  PrimaryBrickRed),
    GoalItem(GoalId.MOVEMENT,   "Movement",   "min",   0, 30, GoalGreen),
    GoalItem(GoalId.WATER,      "Water",      "glasses",0, 8, GoalBlue),
    GoalItem(GoalId.RELAXATION, "Relaxation", "min",   0, 15, GoalPurple),
    GoalItem(GoalId.SLEEP,      "Sleep",      "hrs",   0, 8,  GoalIndigo)
)
