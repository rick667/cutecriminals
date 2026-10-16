package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.PlateSkinType
import com.example.game.ScrewGameViewModel
import com.example.game.ScrewSkinType
import com.example.ui.theme.*

@Composable
fun CosmeticsStoreDialog(
    viewModel: ScrewGameViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableIntStateOf(0) } // 0 = Parafusos, 1 = Placas

    JuicyDialog(
        type = JuicyModalType.COMMON_LARGE,
        onDismiss = onDismiss,
        testTag = "cosmetics_store_dialog"
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PastelMintLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = PastelMintDark, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Ateliê & Skins Cozy",
                        color = CozyTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Texturas Suaves & Acabamentos Pastéis",
                        color = CozyTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Coins indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(PastelVanillaBg, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ScrewGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${state.coins}",
                    color = CozyTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Category selector (Skins de Parafusos / Skins de Placas)
        TabRow(
            selectedTabIndex = selectedCategory,
            containerColor = CozySurfaceSubtle,
            contentColor = CozyTextPrimary,
            modifier = Modifier.clip(RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedCategory == 0,
                onClick = { selectedCategory = 0 },
                text = {
                    Text(
                        "Parafusos & Pinos",
                        fontSize = 12.sp,
                        fontWeight = if (selectedCategory == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedCategory == 0) CozyTextPrimary else CozyTextSecondary
                    )
                }
            )
            Tab(
                selected = selectedCategory == 1,
                onClick = { selectedCategory = 1 },
                text = {
                    Text(
                        "Placas & Superfícies",
                        fontSize = 12.sp,
                        fontWeight = if (selectedCategory == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedCategory == 1) CozyTextPrimary else CozyTextSecondary
                    )
                }
            )
        }

        Spacer(Modifier.height(14.dp))

        // Item List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (selectedCategory == 0) {
                // Screw Skins
                ScrewSkinType.values().forEach { skin ->
                    val isUnlocked = state.unlockedScrewSkins.contains(skin)
                    val isEquipped = state.activeScrewSkin == skin

                    ScrewSkinItemCard(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isEquipped = isEquipped,
                        playerCoins = state.coins,
                        onEquip = { viewModel.equipScrewSkin(skin) },
                        onBuy = { viewModel.buyScrewSkin(skin) }
                    )
                }
            } else {
                // Plate Skins
                PlateSkinType.values().forEach { skin ->
                    val isUnlocked = state.unlockedPlateSkins.contains(skin)
                    val isEquipped = state.activePlateSkin == skin

                    PlateSkinItemCard(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isEquipped = isEquipped,
                        playerCoins = state.coins,
                        onEquip = { viewModel.equipPlateSkin(skin) },
                        onBuy = { viewModel.buyPlateSkin(skin) }
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        JuicyRectangularButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            testTag = "close_cosmetics_btn"
        ) {
            Text("Voltar ao Jogo", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF1B281E))
        }
    }
}

@Composable
fun ScrewSkinItemCard(
    skin: ScrewSkinType,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    playerCoins: Int,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) PastelPowderSkyBg else CozySurfaceSubtle
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 2.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skin Preview Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when (skin) {
                            ScrewSkinType.CLASSIC_METAL -> Brush.radialGradient(listOf(PastelVanillaLight, PastelVanilla, PastelVanillaDark))
                            ScrewSkinType.PRISM_GLASS -> Brush.radialGradient(listOf(Color.White, CozyPorcelainBlue, PastelPowderSkyDark))
                            ScrewSkinType.WOOL_PIN -> Brush.radialGradient(listOf(CozyWoolCream, CozyWoolFelt, CozyDarkWood))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (skin) {
                        ScrewSkinType.CLASSIC_METAL -> Icons.Default.BrightnessLow
                        ScrewSkinType.PRISM_GLASS -> Icons.Default.Diamond
                        ScrewSkinType.WOOL_PIN -> Icons.Default.FiberManualRecord
                    },
                    contentDescription = null,
                    tint = if (skin == ScrewSkinType.WOOL_PIN) CozyDarkWood else CozyTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(skin.displayName, color = CozyTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(skin.subtitle, color = CozyTextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
            }

            Spacer(Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Text(
                        text = "EM USO",
                        color = PastelPowderSkyDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
                isUnlocked -> {
                    JuicyRectangularButton(
                        onClick = onEquip,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("equip_screw_${skin.name}"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("Equipar", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B281E))
                    }
                }
                else -> {
                    JuicyRectangularButton(
                        onClick = onBuy,
                        enabled = playerCoins >= skin.priceCoins,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("buy_screw_${skin.name}"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF1B281E), modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("${skin.priceCoins}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B281E))
                    }
                }
            }
        }
    }
}

@Composable
fun PlateSkinItemCard(
    skin: PlateSkinType,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    playerCoins: Int,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) PastelPowderSkyBg else CozySurfaceSubtle
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 2.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plate Texture Preview
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (skin) {
                            PlateSkinType.PASTEL_ACRYLIC -> Brush.linearGradient(listOf(PastelMint, PastelStrawberryLight))
                            PlateSkinType.POLISHED_WOOD -> Brush.linearGradient(listOf(CozyBirchWood, CozyOakWood))
                            PlateSkinType.ARCTIC_ICE -> Brush.linearGradient(listOf(CozyPorcelainLight, CozyPorcelainBlue))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (skin) {
                        PlateSkinType.PASTEL_ACRYLIC -> Icons.Default.Layers
                        PlateSkinType.POLISHED_WOOD -> Icons.Default.Forest
                        PlateSkinType.ARCTIC_ICE -> Icons.Default.AcUnit
                    },
                    contentDescription = null,
                    tint = if (skin == PlateSkinType.POLISHED_WOOD) CozyDarkWood else CozyTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(skin.displayName, color = CozyTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(skin.subtitle, color = CozyTextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
            }

            Spacer(Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Text(
                        text = "EM USO",
                        color = PastelPowderSkyDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
                isUnlocked -> {
                    JuicyRectangularButton(
                        onClick = onEquip,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("equip_plate_${skin.name}"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("Equipar", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B281E))
                    }
                }
                else -> {
                    JuicyRectangularButton(
                        onClick = onBuy,
                        enabled = playerCoins >= skin.priceCoins,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("buy_plate_${skin.name}"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF1B281E), modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("${skin.priceCoins}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B281E))
                    }
                }
            }
        }
    }
}
