package com.educa.mathkids.presentation.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.YellowStar

@Composable
fun AchievementsScreen(
    playerProgressRepository: PlayerProgressRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rewards by playerProgressRepository.getAllRewards().collectAsState(initial = emptyList())
    val unlockedCount = rewards.count { it.isUnlocked }

    DynamicKidsBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.SpacingLarge)
        ) {
            ScreenHeader(
                title = "🏆 Logros y Medallas",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Resumen de logros
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .border(1.5.dp, YellowStar.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Colección de Trofeos",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateDark
                        )
                        Text(
                            text = "Has desbloqueado $unlockedCount de ${rewards.size} medallas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }
                    Text(text = "🏆 $unlockedCount", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFF57F17))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rewards, key = { it.id }) { reward ->
                    AchievementItemCard(reward = reward)
                }
            }
        }
    }
}

@Composable
private fun AchievementItemCard(reward: Reward) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (reward.isUnlocked) 3.dp else 0.dp, shape)
            .clip(shape)
            .background(if (reward.isUnlocked) SurfaceWhite else Color(0xFFF1F5F9))
            .border(
                width = if (reward.isUnlocked) 1.5.dp else 1.dp,
                color = if (reward.isUnlocked) YellowStar.copy(alpha = 0.5f) else SlateBorder.copy(alpha = 0.5f),
                shape = shape
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (reward.isUnlocked) YellowStar.copy(alpha = 0.15f) else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (reward.isUnlocked) {
                    Text(text = reward.icon, fontSize = 26.sp)
                } else {
                    Text(text = "🔒", fontSize = 20.sp, modifier = Modifier.alpha(0.5f))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reward.isUnlocked) SlateDark else SlateMuted
                )
                Text(
                    text = reward.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (reward.isUnlocked) {
                Text(text = "✅", fontSize = 18.sp)
            }
        }
    }
}
