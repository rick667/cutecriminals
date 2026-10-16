package com.example.pvp

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.ScrewGameViewModel
import com.example.ui.theme.*

@Composable
fun InvasionHubScreen(
    viewModel: ScrewGameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Alvos, 1 = Minhas Defesas
    val opponents = remember(state.currentLevel) {
        OpponentCatalog.getAvailableOpponents(state.currentLevel)
    }

    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Header Superior
            InvasionHeader(
                playerCoins = state.coins,
                onBack = onBack
            )

            Spacer(Modifier.height(14.dp))

            // 2. Tabs de Navegação: Quadro de Alvos vs Minhas Defesas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CozySurfaceSubtle, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    title = "🎯 Quadro de Alvos",
                    isSelected = selectedTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = 0 }
                )
                TabButton(
                    title = "🛡️ Minhas Defesas",
                    isSelected = selectedTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = 1 }
                )
            }

            Spacer(Modifier.height(16.dp))

            // 3. Conteúdo da Tab
            when (selectedTab) {
                0 -> {
                    OpponentsList(
                        opponents = opponents,
                        onInvade = { opp ->
                            viewModel.startInvasion(opp)
                        }
                    )
                }
                1 -> {
                    MyDefensesPanel(
                        profile = state.playerSecurityProfile,
                        playerCoins = state.coins,
                        onUpgrade = { upgradeType ->
                            viewModel.upgradePlayerSecurity(upgradeType)
                        }
                    )
                }
            }
        }

        // 4. Modal Gacha de Recompensa Cega (3 Cofres)
        state.invasionGachaState?.let { gacha ->
            InvasionGachaDialog(
                gachaState = gacha,
                onSelectSafe = { index ->
                    viewModel.selectInvasionGachaSafe(index)
                },
                onCollectAndReturn = {
                    viewModel.collectInvasionRewardAndReturn()
                }
            )
        }
    }
}

@Composable
private fun InvasionHeader(
    playerCoins: Int,
    onBack: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CozySurface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.example.ui.JuicyRoundButton(
                    onClick = onBack,
                    size = 40.dp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Central de Invasões",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = CozyTextPrimary
                    )
                    Text(
                        text = "PvP Assíncrono • Ladrão Cavalheiro",
                        fontSize = 11.sp,
                        color = CozyTextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(PastelMintLight, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🪙 $playerCoins",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp,
                    color = CozyTextPrimary
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CozySurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) CozyTextPrimary else CozyTextMuted
        )
    }
}

@Composable
private fun OpponentsList(
    opponents: List<InvasionOpponent>,
    onInvade: (InvasionOpponent) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(opponents) { opponent ->
            OpponentCard(opponent = opponent, onInvade = { onInvade(opponent) })
        }
    }
}

@Composable
private fun OpponentCard(
    opponent: InvasionOpponent,
    onInvade: () -> Unit
) {
    val prof = opponent.securityProfile

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CozySurface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CardBorderCozy),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar do rival
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFEFE8E0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = opponent.avatarEmoji, fontSize = 26.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opponent.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = CozyTextPrimary
                    )
                    Text(
                        text = opponent.title,
                        fontSize = 11.sp,
                        color = CozyTextSecondary
                    )
                }

                // Nível de Defesa
                Surface(
                    color = Color(0x22E5B83B),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "🛡️ ${prof.totalDefenseRating} Def",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB8860B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Chips com upgrades de segurança do oponente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (prof.extraLocksLevel > 0) {
                    SecurityChip("🔒 +${prof.extraLocksLevel} Travas")
                }
                if (prof.fastAlarmLevel > 0) {
                    SecurityChip("🚨 Alarme ${prof.getAlarmSeconds()}s")
                }
                if (prof.decoyScrewsLevel > 0) {
                    SecurityChip("🔩 Parafusos Turbo")
                }
                if (prof.runeCipherLevel > 0) {
                    SecurityChip("🗝️ Runa Cofre")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Recompensa potencial e Botão de Invadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Espólio: 🪙 ${opponent.rewardCoins} / 🖼️ Relíquia",
                    fontSize = 11.sp,
                    color = CozyTextMuted,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onInvade,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelMint),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Bolt, null, tint = CozyTextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Invadir", color = CozyTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SecurityChip(label: String) {
    Surface(
        color = CozySurfaceSubtle,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = CozyTextPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun MyDefensesPanel(
    profile: SecurityProfile,
    playerCoins: Int,
    onUpgrade: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Card de Visão Geral do Esconderijo do Jogador
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CozySurface),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Segurança do Meu Esconderijo",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            color = CozyTextPrimary
                        )
                        Text(
                            text = "Impeça outros ladrões de roubarem suas obras!",
                            fontSize = 11.sp,
                            color = CozyTextSecondary
                        )
                    }

                    Text(
                        text = "🛡️ ${profile.totalDefenseRating}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE5B83B)
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Upgrades de Segurança Disponíveis",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CozyTextPrimary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Upgrade 1: Travas Extras
        DefenseUpgradeCard(
            title = "Travas Extras",
            description = "Adiciona camadas de oclusão e placas de aço que barram o invasor.",
            iconEmoji = "🔒",
            currentLevel = profile.extraLocksLevel,
            costCoins = 250,
            canAfford = playerCoins >= 250,
            onUpgrade = { onUpgrade("EXTRA_LOCKS") }
        )

        Spacer(Modifier.height(10.dp))

        // Upgrade 2: Alarme Rápido
        DefenseUpgradeCard(
            title = "Alarme Rápido",
            description = "Reduz o tempo do invasor para desarmar o cofre (${profile.getAlarmSeconds()}s atuais).",
            iconEmoji = "🚨",
            currentLevel = profile.fastAlarmLevel,
            costCoins = 250,
            canAfford = playerCoins >= 250,
            onUpgrade = { onUpgrade("FAST_ALARM") }
        )

        Spacer(Modifier.height(10.dp))

        // Upgrade 3: Parafusos Falsos / Decoy
        DefenseUpgradeCard(
            title = "Parafusos Turbo Decoy",
            description = "Insere parafusos que exigem combos encadeados para serem destravados.",
            iconEmoji = "🔩",
            currentLevel = profile.decoyScrewsLevel,
            costCoins = 250,
            canAfford = playerCoins >= 250,
            onUpgrade = { onUpgrade("DECOY_SCREWS") }
        )

        Spacer(Modifier.height(10.dp))

        // Upgrade 4: Runa Criptografada
        DefenseUpgradeCard(
            title = "Cofre Criptografado",
            description = "Tranca a câmara de relíquias com combinações de doca obrigatórias.",
            iconEmoji = "🗝️",
            currentLevel = profile.runeCipherLevel,
            costCoins = 250,
            canAfford = playerCoins >= 250,
            onUpgrade = { onUpgrade("RUNE_CIPHER") }
        )
    }
}

@Composable
private fun DefenseUpgradeCard(
    title: String,
    description: String,
    iconEmoji: String,
    currentLevel: Int,
    costCoins: Int,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CozySurfaceSubtle),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderCozy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEFE8E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CozyTextPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Nvl $currentLevel/3",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE5B83B)
                    )
                }
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = CozyTextSecondary,
                    maxLines = 2
                )
            }

            Spacer(Modifier.width(8.dp))

            if (currentLevel < 3) {
                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelMint),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("🪙 $costCoins", color = CozyTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            } else {
                Surface(
                    color = Color(0x2238B27A),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "MAX",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = PastelMintDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * Lógica e Interface do Fim de Partida Gacha: 3 Cofres Misteriosos.
 * Tabela de probabilidade: 80% Moedas in-game, 20% Obra de Arte do oponente.
 */
@Composable
fun InvasionGachaDialog(
    gachaState: InvasionGachaState,
    onSelectSafe: (Int) -> Unit,
    onCollectAndReturn: () -> Unit
) {
    com.example.ui.JuicyDialog(
        type = com.example.ui.JuicyModalType.COMMON_LARGE,
        onDismiss = onCollectAndReturn,
        testTag = "invasion_gacha_dialog"
    ) {
        Text(
            text = if (gachaState.isRevealed) "🎉 Espólio Recuperado!" else "🏆 Invasão Bem-Sucedida!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = CozyTextPrimary
        )

        Text(
            text = if (gachaState.isRevealed)
                "Confira o que você ganhou no cofre escolhido:"
            else
                "Escolha 1 dos 3 cofres do ${gachaState.opponent.name}:",
            fontSize = 12.sp,
            color = CozyTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // 3 Baús/Cofres
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 3) {
                val isChosen = gachaState.chosenSafeIndex == i
                val safeReward = gachaState.safes.getOrNull(i)

                SafeBoxView(
                    index = i,
                    isChosen = isChosen,
                    isRevealed = gachaState.isRevealed,
                    reward = safeReward,
                    onClick = {
                        if (!gachaState.isRevealed) {
                            onSelectSafe(i)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        if (gachaState.isRevealed) {
            val wonReward = gachaState.safes.getOrNull(gachaState.chosenSafeIndex ?: -1)
            if (wonReward != null) {
                Surface(
                    color = if (wonReward.isArtPiece) Color(0x33E5B83B) else Color(0x2238B27A),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (wonReward.isArtPiece) Color(0xFFE5B83B) else PastelMint)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = wonReward.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = CozyTextPrimary
                        )
                        Text(
                            text = wonReward.subtitle,
                            fontSize = 10.5.sp,
                            color = CozyTextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            com.example.ui.JuicyRectangularButton(
                onClick = onCollectAndReturn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                testTag = "collect_invasion_reward_btn"
            ) {
                Text(
                    text = "Resgatar e Retornar ao Esconderijo",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SafeBoxView(
    index: Int,
    isChosen: Boolean,
    isRevealed: Boolean,
    reward: SafeReward?,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isChosen) 1.08f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "safeScale"
    )

    Card(
        modifier = Modifier
            .width(82.dp)
            .height(105.dp)
            .scale(scale)
            .clickable(enabled = !isRevealed, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isChosen && isRevealed -> Color(0xFFFFF3D6)
                isRevealed -> Color(0xFFF3ECE4)
                else -> Color(0xFFEAE2D8)
            }
        ),
        border = BorderStroke(
            width = if (isChosen) 2.5.dp else 1.dp,
            color = if (isChosen) Color(0xFFE5B83B) else CardBorderCozy
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isChosen) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isRevealed) {
                // Cofre fechado
                Text(text = "🔒", fontSize = 28.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Cofre ${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyTextPrimary
                )
            } else {
                // Cofre revelado
                if (reward != null) {
                    if (reward.isArtPiece && reward.artPiece != null) {
                        com.example.hideout.ArtThumbnailComposable(
                            artPiece = reward.artPiece,
                            modifier = Modifier.size(36.dp),
                            showPedestalForRelics = true
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "RELÍQUIA!",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB8860B)
                        )
                    } else {
                        Text(text = "🪙", fontSize = 26.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "+${reward.coins}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CozyTextPrimary
                        )
                    }
                }
            }
        }
    }
}
