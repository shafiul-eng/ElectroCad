package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuizData
import com.example.ui.theme.CircuitBorder
import com.example.ui.theme.CircuitEmerald
import com.example.ui.theme.CircuitNavyDark
import com.example.ui.theme.CircuitNavySurface
import com.example.ui.theme.CircuitNavySurfaceVariant
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite

@Composable
fun QuizScreen(modifier: Modifier = Modifier) {
    val questions = QuizData.questions
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var selectedOptionIdx by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentQ = questions[currentQuestionIdx]
    val progress = (currentQuestionIdx + 1).toFloat() / questions.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CircuitNavyDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CircuitEmerald.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = "Quiz",
                    tint = CircuitEmerald
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ইলেকট্রনিক্স কুইজ ও দক্ষতা যাচাই",
                    color = TechWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "শিক্ষার্থীদের জন্য প্রস্তুতিমূলক প্রশ্নাবলি",
                    color = TechTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        if (isQuizCompleted) {
            // Completion Screen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CircuitEmerald)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "অভিনন্দন! কুইজ সমাপ্ত হয়েছে",
                        color = TechWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আপনার স্কোর: $score / ${questions.size}",
                        color = CircuitEmerald,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (score >= 5) "চমৎকার ফলাফল! আপনি ইলেকট্রনিক্স বেসিকে দক্ষ।"
                        else "ভালো চেষ্টা! টিউটোরিয়ালগুলো পড়ে আবারও অনুশীলন করুন।",
                        color = TechTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            currentQuestionIdx = 0
                            selectedOptionIdx = null
                            score = 0
                            isQuizCompleted = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("restart_quiz_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = CircuitNavyDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "পুনরায় কুইজ দিন", color = CircuitNavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "প্রশ্ন ${currentQuestionIdx + 1} / ${questions.size}",
                    color = ElectricCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentQ.categoryBn,
                    color = TechTextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ElectricCyan,
                trackColor = CircuitNavySurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentQ.questionBn,
                        color = TechWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQ.questionEn,
                        color = TechTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            currentQ.optionsBn.forEachIndexed { optIdx, optText ->
                val isSelected = selectedOptionIdx == optIdx
                val isAnswered = selectedOptionIdx != null
                val isCorrect = optIdx == currentQ.correctIndex

                val borderColor = when {
                    !isAnswered -> if (isSelected) ElectricCyan else CircuitBorder
                    isCorrect -> CircuitEmerald
                    isSelected -> DangerRed
                    else -> CircuitBorder
                }

                val bgColor = when {
                    !isAnswered -> CircuitNavySurfaceVariant
                    isCorrect -> CircuitEmerald.copy(alpha = 0.15f)
                    isSelected -> DangerRed.copy(alpha = 0.15f)
                    else -> CircuitNavySurfaceVariant
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !isAnswered) {
                            selectedOptionIdx = optIdx
                            if (optIdx == currentQ.correctIndex) {
                                score++
                            }
                        }
                        .testTag("quiz_option_$optIdx"),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('ক'.code + optIdx).toChar()})",
                            color = if (isAnswered && isCorrect) CircuitEmerald else TechTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optText,
                            color = TechWhite,
                            fontSize = 13.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswered) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = CircuitEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = DangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Explanation & Next Button
            if (selectedOptionIdx != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ব্যাখ্যা ও সমাধান:",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQ.explanationBn,
                            color = TechWhite,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (currentQuestionIdx + 1 < questions.size) {
                            currentQuestionIdx++
                            selectedOptionIdx = null
                        } else {
                            isQuizCompleted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_question_button")
                ) {
                    Text(
                        text = if (currentQuestionIdx + 1 < questions.size) "পরবর্তী প্রশ্ন (Next Question)" else "ফলাফল দেখুন (See Results)",
                        color = CircuitNavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
