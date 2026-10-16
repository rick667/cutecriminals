package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * 5 Abas Oficiais do Menu Inicial (Ordem estrita exigida):
 * 1. Loja
 * 2. Cofre
 * 3. Refúgio (Aba central padrão)
 * 4. Quartel (Defesas & PvP)
 * 5. Eventos (Bloqueada - jogador NÃO tem acesso)
 */
enum class MainMenuTab(
    val label: String,
    val icon: ImageVector,
    val testTag: String,
    val isLocked: Boolean = false
) {
    STORE("Loja", Icons.Default.ShoppingCart, "main_tab_store"),
    VAULT("Cofre", Icons.Default.Lock, "main_tab_vault"),
    HIDEOUT("Refúgio", Icons.Default.Home, "main_tab_hideout"),
    BARRACKS("Quartel", Icons.Default.Security, "main_tab_barracks"),
    EVENTS("Eventos", Icons.Default.Celebration, "main_tab_events", isLocked = true)
}

/**
 * Barra de Navegação Inferior Casual & Cozy:
 * - Totalmente alinhada com o design pastel acolhedor do jogo (tons claros, linho/creme, sem fundos escuros ou bordas pesadas).
 * - 5 abas em ordem estrita: Loja > Cofre > Refúgio > Quartel > Eventos
 * - A aba 'Eventos' fica travada: o jogador NÃO consegue acessar nem clicar nela.
 */
@Composable
fun CasualBottomTabBar(
    selectedTab: MainMenuTab,
    onSelectTab: (MainMenuTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Color(0x12000000),
                spotColor = Color(0x18000000)
            ),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFE8DCC8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainMenuTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                val isLocked = tab.isLocked

                val scaleAnim by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "tabScale_${tab.name}"
                )

                // Cores suaves de destaque quando a aba está ativa (estilo cozy pastel)
                val activeBgColor = when (tab) {
                    MainMenuTab.HIDEOUT -> Color(0xFFE8FAF3)  // Menta suave
                    MainMenuTab.STORE -> Color(0xFFFFFBEA)    // Baunilha suave
                    MainMenuTab.VAULT -> Color(0xFFFFF4EC)    // Pêssego suave
                    MainMenuTab.BARRACKS -> Color(0xFFEEF2FF) // Azul céu suave
                    MainMenuTab.EVENTS -> Color.Transparent
                }

                val activeBorderColor = when (tab) {
                    MainMenuTab.HIDEOUT -> Color(0xFFB5EAD7)
                    MainMenuTab.STORE -> Color(0xFFFFEAA7)
                    MainMenuTab.VAULT -> Color(0xFFFFDAC1)
                    MainMenuTab.BARRACKS -> Color(0xFFC7CEEA)
                    MainMenuTab.EVENTS -> Color.Transparent
                }

                val activeContentColor = when (tab) {
                    MainMenuTab.HIDEOUT -> Color(0xFF2E7D32)
                    MainMenuTab.STORE -> Color(0xFFB78103)
                    MainMenuTab.VAULT -> Color(0xFFE65100)
                    MainMenuTab.BARRACKS -> Color(0xFF3F51B5)
                    MainMenuTab.EVENTS -> Color(0xFFA89F97)
                }

                val inactiveContentColor = Color(0xFF8D7B68)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scaleAnim)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) activeBgColor else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 1.2.dp else 0.dp,
                            color = if (isSelected) activeBorderColor else Color.Transparent,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = !isLocked, // Jogador não consegue acessar se bloqueada!
                            onClick = {
                                if (!isLocked) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSelectTab(tab)
                                }
                            }
                        )
                        .padding(vertical = 6.dp)
                        .testTag(tab.testTag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = when {
                                    isLocked -> Color(0xFFBDBDBD)
                                    isSelected -> activeContentColor
                                    else -> inactiveContentColor
                                },
                                modifier = Modifier
                                    .size(if (tab == MainMenuTab.HIDEOUT && isSelected) 24.dp else 22.dp)
                                    .alpha(if (isLocked) 0.45f else 1f)
                            )

                            // Ícone de cadeado sutil na aba de eventos bloqueada
                            if (isLocked) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color(0xFFE0E0E0), CircleShape)
                                        .border(1.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Bloqueado",
                                        tint = Color(0xFF757575),
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = tab.label,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = when {
                                isLocked -> Color(0xFFBDBDBD)
                                isSelected -> activeContentColor
                                else -> inactiveContentColor
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.alpha(if (isLocked) 0.5f else 1f)
                        )
                    }
                }
            }
        }
    }
}
