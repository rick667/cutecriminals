package com.example.tutorial

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.game.ScrewGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Modal Interativo que gerencia o fluxo das etapas 4 a 7 do Tutorial:
 * - Etapa 4: Seleção da Saga 1 (apenas ela destacada e clicável).
 * - Etapa 5: Seleção da Fase 1 da Saga 1 (apenas ela destacada e clicável).
 * - Etapa 6: Cartinha do Poddle (sem o Poddle em pessoa).
 * - Etapa 7: Jogabilidade da Fase 1 + Recompensa OBRIGATORIAMENTE OURO + Diálogo de consolo e presente do Marcel.
 */
@Composable
fun TutorialSagaFlowModal(
    viewModel: ScrewGameViewModel,
    onCompleteTutorialLevel1: () -> Unit,
    onDismiss: () -> Unit
) {
    var subStep by remember { mutableStateOf(4) } // 4 = Saga 1, 5 = Level 1, 6 = Poddle Letter, 7 = Gameplay/Victory, 71 = Marcel Dialogue
    var showPoddleLetter by remember { mutableStateOf(false) }

    // Estado do quebra-cabeça da Fase 1
    var screwsRemovedCount by remember { mutableIntStateOf(0) }
    var levelWon by remember { mutableStateOf(false) }
    var marcelDialogueIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "tutorialSpotlight")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = { /* Bloqueia saída durante o tutorial obrigatório */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .testTag("tutorial_saga_flow_dialog"),
            contentAlignment = Alignment.Center
        ) {
            when (subStep) {
                // =====================================================================
                // === ETAPA 4: DESTAQUE DA PRIMEIRA SAGA ===
                // =====================================================================
                4 -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .shadow(20.dp, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xFFFBF8F3),
                        border = BorderStroke(2.dp, Color(0xFFE5B83B))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SELECIONE A PRIMEIRA SAGA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF261D16)
                            )
                            Text(
                                text = "Marcel: \"Começaremos pela Mansão do Duque Von Poddle!\"",
                                fontSize = 12.sp,
                                color = Color(0xFF7A6A5C),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(18.dp))

                            // Saga 1 em destaque absoluto (pulsando)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(pulseScale)
                                    .shadow(12.dp, RoundedCornerShape(20.dp))
                                    .border(3.dp, Color(0xFFFFD573), RoundedCornerShape(20.dp))
                                    .clickable {
                                        viewModel.haptics.playScrewClick()
                                        subStep = 5 // Avança para a escolha da Fase 1
                                    }
                                    .testTag("tutorial_saga_1_card"),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(62.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF5EBE1)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.saga1_mansao),
                                            contentDescription = "Saga 1 Mansão",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFEF3C7)
                                        ) {
                                            Text(
                                                text = "SAGA I",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFB45309),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Mansão do Duque",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF261D16)
                                        )
                                        Text(
                                            text = "Fases 1 a 20 • Oficina Secreta",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF7A6A5C)
                                        )
                                    }

                                    Icon(
                                        Icons.Default.TouchApp,
                                        contentDescription = "Toque Aqui",
                                        tint = Color(0xFFD4A343),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Outras sagas bloqueadas (desativadas para o tutorial)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                color = Color(0x66EADBCE),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("Saga II: Iate de Luxo (Bloqueada no Tutorial)", fontSize = 12.sp, color = Color(0xFF757575))
                                }
                            }
                        }
                    }
                }

                // =====================================================================
                // === ETAPA 5: DESTAQUE DA FASE 1 DA PRIMEIRA SAGA ===
                // =====================================================================
                5 -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .shadow(20.dp, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xFFFBF8F3),
                        border = BorderStroke(2.dp, Color(0xFFE5B83B))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SAGA I: MANSÃO DO DUQUE",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF261D16)
                            )
                            Text(
                                text = "Marcel: \"Toque na Fase 1 para iniciarmos a invasão!\"",
                                fontSize = 12.sp,
                                color = Color(0xFF7A6A5C),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(20.dp))

                            // Botão da Fase 1 pulsando em destaque
                            Surface(
                                modifier = Modifier
                                    .size(96.dp)
                                    .scale(pulseScale)
                                    .shadow(12.dp, CircleShape)
                                    .border(3.5.dp, Color(0xFF288C52), CircleShape)
                                    .clickable {
                                        viewModel.haptics.playScrewClick()
                                        subStep = 6 // Etapa 6: Cartinha do Poddle
                                        showPoddleLetter = true
                                    }
                                    .testTag("tutorial_level_1_button"),
                                shape = CircleShape,
                                color = Color(0xFF288C52)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "FASE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xCCFFFFFF))
                                        Text(text = "1", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFF288C52), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Toque na Fase 1 para arrombar o primeiro cofre",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF288C52)
                                )
                            }
                        }
                    }
                }

                // =====================================================================
                // === ETAPA 6: CARTINHA DO DUQUE VON PODDLE ===
                // =====================================================================
                6 -> {
                    TutorialPoddleLetterModal(
                        onDismissLetter = {
                            viewModel.haptics.playScrewClick()
                            subStep = 7 // Inicia jogabilidade da fase 1
                        }
                    )
                }

                // =====================================================================
                // === ETAPA 7: JOGABILIDADE DA FASE 1 + RECOMPENSA OBRIGATORIAMENTE OURO ===
                // =====================================================================
                7 -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .shadow(22.dp, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xFF1E140E),
                        border = BorderStroke(2.dp, Color(0xFFE5B83B))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "FASE 1: O PRIMEIRO COFRE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD573)
                            )
                            Text(
                                text = "Toque nos 3 parafusos para soltar a placa blindada!",
                                fontSize = 11.5.sp,
                                color = Color(0xFFD6C8B8),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(18.dp))

                            // Tabuleiro miniatura de parafusos da Fase 1
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF2B1D14),
                                border = BorderStroke(1.5.dp, Color(0xFF4A3425))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Placa central de carvalho
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .height(120.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (screwsRemovedCount >= 3) Color(0xFF288C52).copy(alpha = 0.2f) else Color(0xFF8D6E53),
                                        border = BorderStroke(2.dp, if (screwsRemovedCount >= 3) Color(0xFF4ADE80) else Color(0xFFD4A343))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (screwsRemovedCount >= 3) {
                                                Text(
                                                    text = "PLACA DESTRAVADA! 🔓",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFF4ADE80)
                                                )
                                            } else {
                                                Text(
                                                    text = "COFRE DO DUQUE",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFAF6EE)
                                                )
                                            }
                                        }
                                    }

                                    // 3 Parafusos interativos
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.75f),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (i in 0 until 3) {
                                            val isRemoved = screwsRemovedCount > i
                                            Surface(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clickable(enabled = !isRemoved) {
                                                        viewModel.haptics.playScrewClick()
                                                        screwsRemovedCount++
                                                        if (screwsRemovedCount >= 3) {
                                                            levelWon = true
                                                            viewModel.addCoins(200) // Recompensa obrigatoriamente ouro!
                                                        }
                                                    }
                                                    .testTag("tutorial_screw_$i"),
                                                shape = CircleShape,
                                                color = if (isRemoved) Color(0x33FFFFFF) else Color(0xFFE84A5F),
                                                border = BorderStroke(
                                                    2.dp,
                                                    if (isRemoved) Color(0x55FFFFFF) else Color(0xFFFF8595)
                                                )
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (isRemoved) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp))
                                                    } else {
                                                        Text("🔩", fontSize = 18.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            if (levelWon) {
                                // Banner de Recompensa: OBRIGATORIAMENTE OURO
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF261D16),
                                    border = BorderStroke(1.5.dp, Color(0xFFF5B025))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "COFRE ARROMBADO! 🎉",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFD573)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF5B025), modifier = Modifier.size(22.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "+200 Moedas de Ouro!",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                viewModel.haptics.playScrewClick()
                                                subStep = 71 // Inicia diálogo do Marcel dizendo que está tudo bem e que dará presente
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Coletar Recompensa", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Toque em cada um dos 3 parafusos vermelhos",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD6C8B8)
                                )
                            }
                        }
                    }
                }

                // =====================================================================
                // === ETAPA 7.1: MARCEL APARECE E DIZ QUE ESTÁ TUDO BEM E DARÁ UMA OBRA ===
                // =====================================================================
                71 -> {
                    val marcelSpeechList = listOf(
                        "Está tudo bem! Às vezes não conseguimos encontrar obras de arte nos cofres de primeira...",
                        "...esses magnatas escondem muito bem seus maiores tesouros!",
                        "Mas não desanime! Você foi excelente soltando aqueles parafusos na sua primeira invasão.",
                        "E para comemorar o nosso início, vou te dar uma bela obra de presente para a gente começar a decorar o nosso refúgio!"
                    )

                    TutorialMarcelDialogue(
                        dialogueTexts = marcelSpeechList,
                        currentTextIndex = marcelDialogueIndex,
                        onNextText = {
                            viewModel.haptics.playScrewClick()
                            if (marcelDialogueIndex < marcelSpeechList.size - 1) {
                                marcelDialogueIndex++
                            } else {
                                onCompleteTutorialLevel1() // Avança para a Etapa 8: Revelação do presente!
                            }
                        }
                    )
                }
            }
        }
    }
}
