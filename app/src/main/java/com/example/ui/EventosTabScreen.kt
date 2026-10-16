package com.example.ui

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.ScrewGameState
import com.example.game.ScrewGameViewModel
import com.example.ui.theme.*

/**
 * Tela de Eventos Temporários (Bloqueada por hora conforme solicitado).
 * Apresenta uma estética de cofre bloqueado / dossiê de acesso restrito com estética cozy heist.
 */
@Composable
fun EventosTabScreen(
    viewModel: ScrewGameViewModel,
    state: ScrewGameState,
    onBackToRefugio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E140E))
    ) {
        // Imagem de fundo aconchegante com atmosfera vintage
        Image(
            painter = painterResource(id = R.drawable.salabackground1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay de vinheta escura para dar foco e profundidade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xCC1A1009),
                            Color(0xEE120A05),
                            Color(0xF6090502)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header estilo Dossiê de Operações
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF2C1C13),
                border = BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFDF80), Color(0xFFC6993B), Color(0xFF7A5416))
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF8D6E63), Color(0xFF3E2723))
                                    ),
                                    CircleShape
                                )
                                .border(1.5.dp, Color(0xFFFFD54F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "CENTRAL DE EVENTOS",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFF8E7),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Temporadas & Torneios Especiais",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD7CCC8)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFB71C1C),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFF8A80))
                    ) {
                        Text(
                            text = "BLOQUEADO 🔒",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Dossiê Central de Bloqueio Cozy
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6EE)),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(2.dp, Color(0xFFC7B199)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selo Central com Parafusos e Cadeado Dourado
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFF3CD), Color(0xFFFFE082), Color(0xFFD4AF37))
                                ),
                                CircleShape
                            )
                            .border(3.dp, Color(0xFF8D6E63), CircleShape)
                            .shadow(6.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Cadeado",
                            tint = Color(0xFF3E2723),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "TEMPORADA EM PREPARAÇÃO",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2C1C13),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "O Duque von Poddle reforçou a segurança dos relógios e galerias da mansão. A central de eventos ao vivo, torneios de invasão e desafios de tempo será liberada em breve!",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Prévia de Eventos Futuros (Teasers)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0EAE1), RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "EM BREVE NA CENTRAL:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF8D6E63),
                            letterSpacing = 0.5.sp
                        )

                        FutureEventItem(
                            emoji = "⏱️",
                            title = "Desafio da Torre do Relógio",
                            type = "MODO CONTRA O TEMPO"
                        )

                        FutureEventItem(
                            emoji = "👑",
                            title = "Torneio dos Mestres Ladrões",
                            type = "RANKING PVP GLOBAL"
                        )

                        FutureEventItem(
                            emoji = "🎨",
                            title = "Caçada às Relíquias Perdidas",
                            type = "COLEÇÃO EXCLUSIVA"
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // Botão Tátil para Retornar ao Refúgio
                    JuicyRectangularButton(
                        onClick = {
                            viewModel.haptics.playScrewClick()
                            onBackToRefugio()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        testTag = "eventos_voltar_refugio_button"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color(0xFF1B281E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "VOLTAR AO REFÚGIO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B281E)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FutureEventItem(
    emoji: String,
    title: String,
    type: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C1C13)
            )
            Text(
                text = type,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8D7B68)
            )
        }
        Surface(
            color = Color(0xFFE0D7C6),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "EM BREVE",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
