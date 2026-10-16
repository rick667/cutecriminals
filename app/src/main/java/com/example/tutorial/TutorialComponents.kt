package com.example.tutorial

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.hideout.HideoutCatalog

/**
 * Componente de Diálogo do Marcel para o Tutorial.
 * Conforme exigido: não escurece excessivamente a tela (alpha sutil 0.35f).
 */
@Composable
fun TutorialMarcelDialogue(
    dialogueTexts: List<String>,
    currentTextIndex: Int,
    onNextText: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marcelPulse")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)) // Fundo suave e aconchegante
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onNextText() }
            .testTag("tutorial_marcel_dialogue_overlay"),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Retrato do Marcel com efeito acolhedor
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .size(92.dp)
                        .offset(y = bobOffset.dp)
                        .shadow(12.dp, CircleShape),
                    shape = CircleShape,
                    color = Color(0xFFF9F5EC),
                    border = BorderStroke(2.5.dp, Color(0xFFE5B83B))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.marcel_explicando),
                        contentDescription = "Marcel Curador",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A1C14),
                    border = BorderStroke(1.dp, Color(0xFFE5B83B))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(
                            text = "Marcel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD573)
                        )
                        Text(
                            text = "Curador do Esconderijo",
                            fontSize = 10.sp,
                            color = Color(0xFFD6C8B8)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Cartão de Balão de Fala do Marcel
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFFAF7F2),
                border = BorderStroke(1.8.dp, Color(0xFFD8C3A5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = dialogueTexts.getOrElse(currentTextIndex) { "" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D231B),
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentTextIndex + 1} de ${dialogueTexts.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8C7A6B)
                        )

                        Button(
                            onClick = onNextText,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF261D16)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (currentTextIndex >= dialogueTexts.size - 1) "Entendido!" else "Continuar ▶",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Cartinha do Duque Von Poddle (Etapa 6 do Tutorial).
 * O Poddle NÃO aparece em pessoa, apenas sua cartinha com selo de cera oficial.
 */
@Composable
fun TutorialPoddleLetterModal(
    onDismissLetter: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissLetter,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(24.dp)
                .testTag("poddle_letter_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(18.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFF9EE), // Textura de pergaminho clássico
                border = BorderStroke(2.5.dp, Color(0xFFC49A45))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selo de cera vermelho vintage
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = Color(0xFFB71C1C),
                        shadowElevation = 6.dp,
                        border = BorderStroke(2.dp, Color(0xFFEF5350))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "👑", fontSize = 26.sp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "AVISO DO DUQUE VON PODDLE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4A2810),
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "Mansão Imperial • Propriedade Privada",
                        fontSize = 11.sp,
                        color = Color(0xFF8D6E53)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFE0C9A6)
                    )

                    // Texto oficial da carta conforme solicitado
                    Text(
                        text = "\"Atenção a todos os invasores, curiosos e gatunos de meia-tigela:\n\nNINGUÉM ouse encostar as patas nos meus cofres e nos meus parafusos blindados! Tudo aqui pertence à nobreza e quem tentar arrombar sairá de mãos vazias!\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF332211),
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "— Assinado: Duque Von Poddle",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A4E2D),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = onDismissLetter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("poddle_letter_start_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF288C52) // Verde para iniciar desafio
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "DESAFIAR COFRE",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal de Revelação da Obra Dada pelo Marcel (Etapa 8 do Tutorial).
 * Dá "Os Jogadores de Cartas" de Cézanne (sagaId = 0, fora da lista de sagas).
 */
@Composable
fun TutorialGiftRevealModal(
    onAcceptGift: () -> Unit
) {
    val giftPiece = remember {
        HideoutCatalog.getDefaultCatalog().firstOrNull { it.id == "homensjogandocartas_art" }
            ?: HideoutCatalog.getDefaultCatalog().first { it.sagaId == 0 }
    }

    Dialog(
        onDismissRequest = onAcceptGift,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(20.dp)
                .testTag("marcel_gift_reveal_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color(0xFFE5B83B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge festivo de presente
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎁", fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "PRESENTE DO MARCEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = giftPiece.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF261D16),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = giftPiece.author,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7A6A5C)
                    )

                    Spacer(Modifier.height(14.dp))

                    // Moldura com a imagem da obra-prima
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2A221A),
                        border = BorderStroke(2.dp, Color(0xFFC6993B))
                    ) {
                        Image(
                            painter = painterResource(id = giftPiece.drawableRes ?: R.drawable.homensjogandocartas_art),
                            contentDescription = giftPiece.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Uma obra-prima clássica fora das sagas para inaugurar as paredes do seu novo refúgio!",
                        fontSize = 12.sp,
                        color = Color(0xFF5A4839),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onAcceptGift,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("accept_gift_and_go_hideout_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF261D16)
                        )
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFFFFD573))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "LEVAR AO REFÚGIO",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
