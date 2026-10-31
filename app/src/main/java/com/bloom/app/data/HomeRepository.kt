package com.bloom.app.data

import androidx.compose.ui.graphics.Color
import com.bloom.app.ui.theme.GoalBlue
import com.bloom.app.ui.theme.GoalGreen
import com.bloom.app.ui.theme.GoalIndigo
import com.bloom.app.ui.theme.GoalPurple
import com.bloom.app.ui.theme.PrimaryBrickRed

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

enum class GoalId { MEALS, MOVEMENT, WATER, RELAXATION, SLEEP }

data class GoalItem(
    val id: GoalId,
    val label: String,
    val unit: String,
    val current: Int,
    val target: Int,
    val ringColor: Color
)

class HomeRepository {

    fun getFaqs(): List<FaqItem> = listOf(
        FaqItem(1, "What exactly is PCOS?",
            "PCOS stands for polycystic ovary syndrome, a hormonal disorder common among women of reproductive age that affects the ovaries and ovulation."),
        FaqItem(2, "What causes PCOS?",
            "The exact cause isn't known, but factors include excess insulin production, low-grade inflammation, heredity, and excess androgen levels."),
        FaqItem(3, "What are the symptoms?",
            "Common symptoms include irregular periods, excess androgen levels causing acne or excess hair, and polycystic ovaries visible on ultrasound."),
        FaqItem(4, "How is PCOS diagnosed?",
            "Doctors typically use a combination of physical exams, blood tests to check hormone levels, and pelvic ultrasound to look for cysts."),
        FaqItem(5, "Can PCOS be cured?",
            "There's no cure, but symptoms can be effectively managed through lifestyle changes such as diet and exercise, and medication if needed."),
        FaqItem(6, "How does diet affect PCOS?",
            "A low-glycaemic diet can help manage insulin levels and reduce inflammation, which in turn can ease symptoms and support hormonal balance."),
        FaqItem(7, "Does exercise help PCOS?",
            "Yes — regular exercise improves insulin sensitivity, helps maintain a healthy weight, and can help regulate your menstrual cycle over time."),
        FaqItem(8, "What is insulin resistance?",
            "Insulin resistance means your cells don't respond normally to insulin, so your body produces more. This is common in PCOS and drives many symptoms."),
        FaqItem(9, "Can I still get pregnant with PCOS?",
            "Yes, many women with PCOS conceive naturally or with lifestyle changes. Fertility treatments are available for those who need extra support."),
        FaqItem(10, "How does stress affect PCOS?",
            "Stress raises cortisol levels, which can worsen hormonal imbalances, disrupt ovulation, and intensify PCOS symptoms over time.")
    )

    fun getDefaultGoals(): List<GoalItem> = listOf(
        GoalItem(GoalId.MEALS,      "Meals",      "meals", 0, 3,  PrimaryBrickRed),
        GoalItem(GoalId.MOVEMENT,   "Movement",   "min",   0, 30, GoalGreen),
        GoalItem(GoalId.WATER,      "Water",      "glasses",0, 8, GoalBlue),
        GoalItem(GoalId.RELAXATION, "Relaxation", "min",   0, 15, GoalPurple),
        GoalItem(GoalId.SLEEP,      "Sleep",      "hrs",   0, 8,  GoalIndigo)
    )
}
