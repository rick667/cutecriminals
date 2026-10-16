package com.example.ui

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ScrewGameState
import com.example.game.ScrewGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tela de Loja Organizada por Áreas (sem abas).
 * 1ª Área: Assinaturas (Aprendiz, Pro e Mestre) lado a lado em ícones quadrados.
 * 2ª Área: Compra de pacotes de chaves.
 * 3ª Área: Compra de pacotes de ouro.
 * 4ª Área: Compra de especiais.
 * Todas as compras exclusivamente com dinheiro real (R$), integradas diretamente
 * com o Google Play Billing para pagamento em 1 clique com cartão de crédito cadastrado.
 */
@Composable
fun LojaTabScreen(
    viewModel: ScrewGameViewModel,
    state: ScrewGameState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var selectedItemForBilling by remember { mutableStateOf<PlayStoreItem?>(null) }
    var lastPurchasedNotification by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EE))
            .statusBarsPadding()
            .testTag("store_tab_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 96.dp)
        ) {
            // Header sutil da Loja com saldo atual de recursos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Loja do Mercado Negro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF261D16)
                    )
                    Text(
                        text = "Cobrança rápida e segura via Google Play",
                        fontSize = 11.5.sp,
                        color = Color(0xFF7A6A5C)
                    )
                }

                // Saldo de recursos atual do jogador
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE8DAC8))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VpnKey,
                                contentDescription = "Chaves",
                                tint = Color(0xFFD4A343),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${state.keys}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261D16)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE8DAC8))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Ouro",
                                tint = Color(0xFFF5B025),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${state.coins}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261D16)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // =========================================================================
            // === 1ª ÁREA: ASSINATURAS (3 LADO A LADO EM ÍCONES QUADRADOS) ===
            // =========================================================================
            StoreSectionHeader(
                title = "Assinaturas VIP",
                subtitle = "3 planos exclusivos em ícones quadrados",
                iconEmoji = "👑"
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Aprendiz
                SquareSubscriptionCard(
                    title = "Aprendiz",
                    price = "R$ 9,90",
                    period = "/mês",
                    iconEmoji = "🥉",
                    badge = "POPULAR",
                    bgColor = Color(0xFFFFF7ED),
                    borderColor = Color(0xFFFFD8A8),
                    modifier = Modifier.weight(1f),
                    testTag = "sub_aprendiz_card",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "sub_aprendiz_monthly",
                            title = "Assinatura Aprendiz",
                            subtitle = "Plano VIP Mensal Aprendiz",
                            priceFormatted = "R$ 9,90/mês",
                            type = StoreProductType.SUBSCRIPTION,
                            keysGranted = 20,
                            coinsGranted = 2500,
                            iconEmoji = "🥉"
                        )
                    }
                )

                // 2. Pro
                SquareSubscriptionCard(
                    title = "Pro",
                    price = "R$ 19,90",
                    period = "/mês",
                    iconEmoji = "🥈",
                    badge = "MELHOR VALOR",
                    bgColor = Color(0xFFF0FDF4),
                    borderColor = Color(0xFFBBF7D0),
                    modifier = Modifier.weight(1f),
                    testTag = "sub_pro_card",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "sub_pro_monthly",
                            title = "Assinatura Pro",
                            subtitle = "Plano VIP Mensal Pro",
                            priceFormatted = "R$ 19,90/mês",
                            type = StoreProductType.SUBSCRIPTION,
                            keysGranted = 60,
                            coinsGranted = 8000,
                            iconEmoji = "🥈"
                        )
                    }
                )

                // 3. Mestre
                SquareSubscriptionCard(
                    title = "Mestre",
                    price = "R$ 34,90",
                    period = "/mês",
                    iconEmoji = "🥇",
                    badge = "ELITE",
                    bgColor = Color(0xFFFEFCE8),
                    borderColor = Color(0xFFFEF08A),
                    modifier = Modifier.weight(1f),
                    testTag = "sub_mestre_card",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "sub_mestre_monthly",
                            title = "Assinatura Mestre",
                            subtitle = "Plano VIP Mensal Mestre",
                            priceFormatted = "R$ 34,90/mês",
                            type = StoreProductType.SUBSCRIPTION,
                            keysGranted = 150,
                            coinsGranted = 25000,
                            iconEmoji = "🥇"
                        )
                    }
                )
            }

            Spacer(Modifier.height(26.dp))

            // =========================================================================
            // === 2ª ÁREA: PACOTES DE CHAVES ===
            // =========================================================================
            StoreSectionHeader(
                title = "Pacotes de Chaves",
                subtitle = "Chaves mestras para destravar cofres e jogar sagas",
                iconEmoji = "🗝️"
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResourcePurchaseCard(
                    title = "15 Chaves",
                    subtitle = "Básico",
                    price = "R$ 4,90",
                    iconEmoji = "🗝️",
                    bonusBadge = null,
                    modifier = Modifier.weight(1f),
                    testTag = "keys_pack_15",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "keys_pack_15",
                            title = "Pacote 15 Chaves",
                            subtitle = "Destrava níveis e cofres misteriosos",
                            priceFormatted = "R$ 4,90",
                            type = StoreProductType.KEYS,
                            keysGranted = 15,
                            iconEmoji = "🗝️"
                        )
                    }
                )

                ResourcePurchaseCard(
                    title = "60 Chaves",
                    subtitle = "Aventureiro",
                    price = "R$ 14,90",
                    iconEmoji = "🗝️",
                    bonusBadge = "+10 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "keys_pack_60",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "keys_pack_60",
                            title = "Pacote 60 Chaves",
                            subtitle = "Inclui 10 chaves bônus grátis",
                            priceFormatted = "R$ 14,90",
                            type = StoreProductType.KEYS,
                            keysGranted = 60,
                            badgeLabel = "+10 BÔNUS",
                            iconEmoji = "🗝️"
                        )
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResourcePurchaseCard(
                    title = "200 Chaves",
                    subtitle = "Cofre de Chaves",
                    price = "R$ 34,90",
                    iconEmoji = "🗝️",
                    bonusBadge = "+50 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "keys_pack_200",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "keys_pack_200",
                            title = "Pacote 200 Chaves",
                            subtitle = "Cofre abundante para assaltos",
                            priceFormatted = "R$ 34,90",
                            type = StoreProductType.KEYS,
                            keysGranted = 200,
                            badgeLabel = "+50 BÔNUS",
                            iconEmoji = "🗝️"
                        )
                    }
                )

                ResourcePurchaseCard(
                    title = "700 Chaves",
                    subtitle = "Baú do Mestre",
                    price = "R$ 89,90",
                    iconEmoji = "🗝️",
                    bonusBadge = "+200 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "keys_pack_700",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "keys_pack_700",
                            title = "Pacote 700 Chaves",
                            subtitle = "Reserva definitiva de chaves",
                            priceFormatted = "R$ 89,90",
                            type = StoreProductType.KEYS,
                            keysGranted = 700,
                            badgeLabel = "+200 BÔNUS",
                            iconEmoji = "🗝️"
                        )
                    }
                )
            }

            Spacer(Modifier.height(26.dp))

            // =========================================================================
            // === 3ª ÁREA: COMPRA DE OURO ===
            // =========================================================================
            StoreSectionHeader(
                title = "Compra de Ouro",
                subtitle = "Moedas de ouro para defesas, melhorias e artes",
                iconEmoji = "🪙"
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResourcePurchaseCard(
                    title = "1.000 Ouro",
                    subtitle = "Punhado",
                    price = "R$ 4,90",
                    iconEmoji = "🪙",
                    bonusBadge = null,
                    modifier = Modifier.weight(1f),
                    testTag = "gold_pack_1000",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "gold_pack_1000",
                            title = "1.000 Moedas de Ouro",
                            subtitle = "Punhado de ouro instantâneo",
                            priceFormatted = "R$ 4,90",
                            type = StoreProductType.GOLD,
                            coinsGranted = 1000,
                            iconEmoji = "🪙"
                        )
                    }
                )

                ResourcePurchaseCard(
                    title = "5.500 Ouro",
                    subtitle = "Bolsa de Ouro",
                    price = "R$ 16,90",
                    iconEmoji = "💰",
                    bonusBadge = "+500 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "gold_pack_5500",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "gold_pack_5500",
                            title = "5.500 Moedas de Ouro",
                            subtitle = "Inclui 500 moedas bônus",
                            priceFormatted = "R$ 16,90",
                            type = StoreProductType.GOLD,
                            coinsGranted = 5500,
                            badgeLabel = "+500 BÔNUS",
                            iconEmoji = "💰"
                        )
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResourcePurchaseCard(
                    title = "23.000 Ouro",
                    subtitle = "Baú do Tesouro",
                    price = "R$ 49,90",
                    iconEmoji = "🏆",
                    bonusBadge = "+3.000 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "gold_pack_23000",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "gold_pack_23000",
                            title = "23.000 Moedas de Ouro",
                            subtitle = "Grande baú do tesouro com bônus",
                            priceFormatted = "R$ 49,90",
                            type = StoreProductType.GOLD,
                            coinsGranted = 23000,
                            badgeLabel = "+3.000 BÔNUS",
                            iconEmoji = "🏆"
                        )
                    }
                )

                ResourcePurchaseCard(
                    title = "90.000 Ouro",
                    subtitle = "Reserva Imperial",
                    price = "R$ 129,90",
                    iconEmoji = "👑",
                    bonusBadge = "+15.000 BÔNUS",
                    modifier = Modifier.weight(1f),
                    testTag = "gold_pack_90000",
                    onClick = {
                        viewModel.haptics.playScrewClick()
                        selectedItemForBilling = PlayStoreItem(
                            id = "gold_pack_90000",
                            title = "90.000 Moedas de Ouro",
                            subtitle = "A maior reserva imperial de ouro",
                            priceFormatted = "R$ 129,90",
                            type = StoreProductType.GOLD,
                            coinsGranted = 90000,
                            badgeLabel = "+15.000 BÔNUS",
                            iconEmoji = "👑"
                        )
                    }
                )
            }

            Spacer(Modifier.height(26.dp))

            // =========================================================================
            // === 4ª ÁREA: ESPECIAIS & PACOTES ESPECIAIS ===
            // =========================================================================
            StoreSectionHeader(
                title = "Especiais & Ofertas",
                subtitle = "Combos exclusivos e pacotes raros de assalto",
                iconEmoji = "⭐"
            )

            Spacer(Modifier.height(10.dp))

            SpecialBundleCard(
                title = "Pacote de Boas-Vindas",
                description = "30 Chaves + 3.500 Moedas de Ouro + Escudo Inicial",
                price = "R$ 9,90",
                badge = "OFERTA ÚNICA",
                iconEmoji = "🎁",
                testTag = "special_welcome_bundle",
                onClick = {
                    viewModel.haptics.playScrewClick()
                    selectedItemForBilling = PlayStoreItem(
                        id = "bundle_welcome",
                        title = "Pacote de Boas-Vindas",
                        subtitle = "30 Chaves + 3.500 Ouro + Escudo",
                        priceFormatted = "R$ 9,90",
                        type = StoreProductType.SPECIAL,
                        keysGranted = 30,
                        coinsGranted = 3500,
                        iconEmoji = "🎁"
                    )
                }
            )

            Spacer(Modifier.height(10.dp))

            SpecialBundleCard(
                title = "Passe do Ladrão Cavalheiro",
                description = "100 Chaves + 12.000 Moedas de Ouro + Relíquia Especial",
                price = "R$ 24,90",
                badge = "SUPER COMBO",
                iconEmoji = "🎩",
                testTag = "special_gentleman_bundle",
                onClick = {
                    viewModel.haptics.playScrewClick()
                    selectedItemForBilling = PlayStoreItem(
                        id = "bundle_gentleman",
                        title = "Passe do Ladrão Cavalheiro",
                        subtitle = "100 Chaves + 12.000 Ouro + Relíquia",
                        priceFormatted = "R$ 24,90",
                        type = StoreProductType.SPECIAL,
                        keysGranted = 100,
                        coinsGranted = 12000,
                        iconEmoji = "🎩"
                    )
                }
            )

            Spacer(Modifier.height(10.dp))

            SpecialBundleCard(
                title = "Mega Pacote Mestre dos Cofres",
                description = "300 Chaves + 40.000 Ouro + Insígnia Dourada Exclusiva",
                price = "R$ 54,90",
                badge = "EDIÇÃO LIMITADA",
                iconEmoji = "💎",
                testTag = "special_vault_master_bundle",
                onClick = {
                    viewModel.haptics.playScrewClick()
                    selectedItemForBilling = PlayStoreItem(
                        id = "bundle_vault_master",
                        title = "Mega Pacote Mestre dos Cofres",
                        subtitle = "300 Chaves + 40.000 Ouro + Insígnia",
                        priceFormatted = "R$ 54,90",
                        type = StoreProductType.SPECIAL,
                        keysGranted = 300,
                        coinsGranted = 40000,
                        iconEmoji = "💎"
                    )
                }
            )
        }

        // Notificação de compra bem-sucedida via Google Play
        AnimatedVisibility(
            visible = lastPurchasedNotification != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4ADE80),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = lastPurchasedNotification ?: "",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Modal de Cobrança 1-Clique Oficial do Google Play
        selectedItemForBilling?.let { item ->
            GooglePlayBillingSheet(
                item = item,
                onConfirmPurchase = { purchasedItem ->
                    if (purchasedItem.keysGranted > 0) {
                        viewModel.addKeys(purchasedItem.keysGranted)
                    }
                    if (purchasedItem.coinsGranted > 0) {
                        viewModel.addCoins(purchasedItem.coinsGranted)
                    }
                    viewModel.haptics.playScrewClick()
                    lastPurchasedNotification = "Compra de ${purchasedItem.title} aprovada via Google Play!"
                    coroutineScope.launch {
                        delay(3500)
                        lastPurchasedNotification = null
                    }
                },
                onDismiss = { selectedItemForBilling = null }
            )
        }
    }
}

/**
 * Título de Seção / Área da Loja
 */
@Composable
private fun StoreSectionHeader(
    title: String,
    subtitle: String,
    iconEmoji: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE8DAC8))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = iconEmoji, fontSize = 17.sp)
            }
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF261D16)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF7A6A5C)
            )
        }
    }
}

/**
 * Card Quadrado de Assinatura (Aprendiz, Pro, Mestre lado a lado)
 */
@Composable
private fun SquareSubscriptionCard(
    title: String,
    price: String,
    period: String,
    iconEmoji: String,
    badge: String,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(0.92f) // Ícone em formato quadrado harmonioso
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Badge superior compacto
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.85f)
            ) {
                Text(
                    text = badge,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF8D6218),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // Ícone central em destaque
            Text(
                text = iconEmoji,
                fontSize = 28.sp
            )

            // Título do plano (Aprendiz, Pro, Mestre)
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF261D16),
                textAlign = TextAlign.Center
            )

            // Botão com valor em dinheiro real
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF261D16)
            ) {
                Text(
                    text = "$price$period",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Card de Compra de Chaves ou Ouro
 */
@Composable
private fun ResourcePurchaseCard(
    title: String,
    subtitle: String,
    price: String,
    iconEmoji: String,
    bonusBadge: String?,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8DAC8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bonusBadge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = bonusBadge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
            } else {
                Spacer(Modifier.height(16.dp))
            }

            Text(text = iconEmoji, fontSize = 28.sp)

            Spacer(Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF261D16)
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF7A6A5C)
            )

            Spacer(Modifier.height(10.dp))

            // Botão com preço oficial
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF01875F) // Verde Google Play
            ) {
                Text(
                    text = price,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Card de Pacote Especial / Combo
 */
@Composable
private fun SpecialBundleCard(
    title: String,
    description: String,
    price: String,
    badge: String,
    iconEmoji: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.2.dp, Color(0xFFFFD573))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = iconEmoji, fontSize = 24.sp)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF261D16)
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = Color(0xFF7A6A5C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF01875F) // Verde Google Play
            ) {
                Text(
                    text = price,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
