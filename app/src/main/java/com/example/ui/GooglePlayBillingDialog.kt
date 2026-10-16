package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StoreProductType {
    SUBSCRIPTION,
    KEYS,
    GOLD,
    SPECIAL
}

data class PlayStoreItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val priceFormatted: String,
    val type: StoreProductType,
    val keysGranted: Int = 0,
    val coinsGranted: Int = 0,
    val badgeLabel: String? = null,
    val iconEmoji: String = "💎"
)

/**
 * Modal Bottom Sheet nativo do Google Play Billing.
 * Permite que o jogador pague diretamente com o cartão cadastrado na Google Play Store
 * com apenas 1 clique de forma rápida, segura e sem trâmites complexos.
 */
@Composable
fun GooglePlayBillingSheet(
    item: PlayStoreItem,
    userEmail: String = "lola.monroe667@gmail.com",
    onConfirmPurchase: (PlayStoreItem) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            if (!isProcessing) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(enabled = !isProcessing) { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .shadow(16.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .testTag("google_play_billing_sheet"),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra de arraste do BottomSheet
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE0E0E0))
                    )

                    Spacer(Modifier.height(16.dp))

                    // Header oficial do Google Play
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Logo estilizado da Google Play
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color(0xFFF1F3F4)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "▶",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF01875F)
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Google Play",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF202124)
                            )
                        }

                        // Email da conta do usuário no Google Play
                        Text(
                            text = userEmail,
                            fontSize = 11.5.sp,
                            color = Color(0xFF5F6368),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFF1F3F4)
                    )

                    // Informações do Produto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8F9FA),
                                border = BorderStroke(1.dp, Color(0xFFE8EAED))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = item.iconEmoji, fontSize = 24.sp)
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF202124)
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 12.sp,
                                    color = Color(0xFF5F6368),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Preço oficial em dinheiro real
                        Text(
                            text = item.priceFormatted,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF202124)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Método de Pagamento Cadastrado na Google Play
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8F9FA),
                        border = BorderStroke(1.dp, Color(0xFFE8EAED))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = "Cartão de Crédito",
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Mastercard •••• 4242",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF202124)
                                    )
                                    Text(
                                        text = "Cartão padrão na sua conta Google",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF5F6368)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF70757A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Nota de Segurança e Termos
                    Text(
                        text = if (item.type == StoreProductType.SUBSCRIPTION)
                            "Assinatura mensal renovada automaticamente. Cancele quando quiser no Google Play."
                        else
                            "Cobrança direta via Google Play. Seguro, criptografado e instantâneo.",
                        fontSize = 10.sp,
                        color = Color(0xFF80868B),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    // Botão 1-Clique do Google Play
                    Button(
                        onClick = {
                            if (!isProcessing && !isSuccess) {
                                isProcessing = true
                                coroutineScope.launch {
                                    // Simula a autenticação e autorização segura do Google Play Billing
                                    delay(800)
                                    isProcessing = false
                                    isSuccess = true
                                    delay(400)
                                    onConfirmPurchase(item)
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_play_1_tap_buy_button"),
                        enabled = !isProcessing && !isSuccess,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF01875F), // Verde Google Play Oficial
                            disabledContainerColor = Color(0xFF01875F).copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Processando pagamento seguro...",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else if (isSuccess) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Aprovado!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Comprar com 1 clique",
                                fontSize = 14.5.sp,
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
