package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.*
import com.example.hideout.*
import com.example.haptics.HapticFeedbackManager
import com.example.ui.theme.*
import com.example.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthViewModel
import com.example.auth.LoginScreen
import com.example.tutorial.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainMenuScreen(
    viewModel: ScrewGameViewModel,
    state: ScrewGameState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Interactive Hideout State
    var catalog by remember {
        mutableStateOf(
            HideoutCatalog.getDefaultCatalog().map { piece ->
                piece.copy(isUnlocked = piece.isUnlocked)
            }
        )
    }

    var placedPieces by remember {
        mutableStateOf<List<PlacedArtPiece>>(emptyList())
    }
    // Conforme solicitado: o primeiro background de refúgio do jogador é o 4 (o 3 será o mais caro de todos)
    var backgroundIndex by remember { mutableStateOf(4) }

    // Estado e etapas do Tutorial Oficial (1 a 10)
    var tutorialStep by remember {
        mutableStateOf(TutorialManager.getCurrentStep(context))
    }
    var marcelOpeningIndex by remember { mutableIntStateOf(0) }
    var marcelFarewellIndex by remember { mutableIntStateOf(0) }
    var showTutorialSagaModal by remember { mutableStateOf(false) }
    var showTutorialGiftModal by remember { mutableStateOf(false) }

    // Etapa 1 do Tutorial: Primeiro contato na tela inicial sem interação por poucos segundos
    LaunchedEffect(tutorialStep) {
        if (tutorialStep == TutorialStep.STEP_1_INITIAL_FREEZE) {
            delay(1800)
            tutorialStep = TutorialStep.STEP_2_MARCEL_DIALOGUE
            TutorialManager.setStep(context, tutorialStep)
        }
    }

    val authViewModel: AuthViewModel = viewModel()
    var showArtworksModal by remember { mutableStateOf(false) }
    var showHideoutAdminModal by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLoginModal by remember { mutableStateOf(false) }
    var selectedPieceId by remember { mutableStateOf<String?>(null) }
    var draggingPieceId by remember { mutableStateOf<String?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragValid by remember { mutableStateOf(true) }
    var activeTab by remember { mutableStateOf(MainMenuTab.HIDEOUT) }

    androidx.activity.compose.BackHandler(enabled = activeTab != MainMenuTab.HIDEOUT) {
        activeTab = MainMenuTab.HIDEOUT
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3ECE4))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val topAreaHeight = screenHeight * 0.10f
        val bottomAreaHeight = screenHeight * 0.25f
        // O quarto se estende até a parte inferior, penetrando atrás da HUD para dar profundidade
        val roomAreaHeight = screenHeight * 0.72f

        val roomScrollState = rememberScrollState(initial = 120)
        val roomWidthDp = 1280.dp

        val screenHeightDp = screenHeight.value
        val topAreaHeightDp = topAreaHeight.value
        // Borda de madeira inferior da HUD do menu começa aproximadamente aos 70% da altura da tela
        val hudTopScreenY = screenHeightDp * 0.70f
        // Posição correspondente dentro do Box do quarto (offset Y = topAreaHeight):
        val hudTopInRoomY = hudTopScreenY - topAreaHeightDp
        // O piso onde repousa a base de estátuas e pedestais, ficando ligeiramente sobreposto (22dp) pela HUD:
        val floorTargetBottomY = hudTopInRoomY + 55f

        val wallZoneTop = 15f
        val wallZoneBottom = hudTopInRoomY - 70f
        val floorTargetY = floorTargetBottomY // Base Y onde descansam as esculturas e relíquias

        // Validação de posicionamento:
        // - Relíquias e esculturas só se movem na horizontal (seu Y é travado rigorosamente no piso).
        // - Não permite sobreposição (nem criação, nem movimento) entre obras da mesma zona.
        fun validatePlacement(piece: ArtPiece, posX: Float, posY: Float, ignoreId: String? = null): Boolean {
            val fitsZone = when (piece.zone) {
                PlacementZone.WALL -> posY >= wallZoneTop - 10f && (posY + piece.heightDp) <= wallZoneBottom + 20f
                PlacementZone.FLOOR -> true // No chão, o Y é fixo na linha do piso
            }
            if (!fitsZone) return false

            val actualY = if (piece.zone == PlacementZone.FLOOR) (floorTargetBottomY - piece.heightDp) else posY

            val candidate = PlacedArtPiece(
                artPieceId = piece.id,
                positionX = posX,
                positionY = actualY,
                widthDp = piece.widthDp,
                heightDp = piece.heightDp,
                zone = piece.zone
            )
            val collides = placedPieces.any { existing ->
                if (existing.artPieceId == ignoreId) false
                else candidate.intersects(existing)
            }
            return !collides
        }

        /**
         * Encontra uma posição horizontal desobstruída no quarto para uma nova obra do inventário.
         * Garante que nenhuma obra do mesmo tipo ou zona nasça em cima de outra existente.
         */
        fun findSafeSpawnPosition(piece: ArtPiece): Pair<Float, Float>? {
            val targetY = if (piece.zone == PlacementZone.WALL) 70f else (floorTargetBottomY - piece.heightDp)
            val margin = 20f
            val maxRoomX = roomWidthDp.value - piece.widthDp - margin

            // Tenta primeiro em pontos padrão confortáveis, depois varre horizontalmente
            val preferredSpots = listOf(140f, 380f, 620f, 860f, 1080f)
            for (spotX in preferredSpots) {
                val clampedX = spotX.coerceIn(margin, maxRoomX)
                if (validatePlacement(piece, clampedX, targetY)) {
                    return Pair(clampedX, targetY)
                }
            }

            // Varredura linear fina caso as posições preferenciais estejam ocupadas
            var scanX = margin
            while (scanX <= maxRoomX) {
                if (validatePlacement(piece, scanX, targetY)) {
                    return Pair(scanX, targetY)
                }
                scanX += 40f
            }
            return null
        }

        LaunchedEffect(screenHeight) {
            val saved = HideoutSaveManager.loadHideout(context)
            if (saved != null) {
                // Ajusta peças de chão salvas para a altura correta do piso na HUD inferior
                val adjustedPieces = saved.placedPieces.map { placed ->
                    val art = catalog.firstOrNull { it.id == placed.artPieceId || it.id == HideoutCatalog.migrateId(placed.artPieceId) }
                    if (art != null && art.zone == PlacementZone.FLOOR) {
                        val idealY = floorTargetBottomY - art.heightDp
                        // Se a peça foi salva na posição antiga flutuando alto ou fora dos limites da HUD
                        if (placed.positionY + art.heightDp < hudTopInRoomY || placed.positionY + art.heightDp > floorTargetBottomY + 25f) {
                            placed.copy(positionY = idealY)
                        } else {
                            placed
                        }
                    } else {
                        placed
                    }
                }
                placedPieces = adjustedPieces
                backgroundIndex = saved.backgroundIndex
                catalog = catalog.map { piece ->
                    val isUnlocked = saved.unlockedArtIds.contains(piece.id)
                    val isPlaced = adjustedPieces.any { it.artPieceId == piece.id || HideoutCatalog.migrateId(it.artPieceId) == piece.id }
                    piece.copy(isUnlocked = isUnlocked, isPlaced = isPlaced)
                }
                if (adjustedPieces != saved.placedPieces) {
                    saveHideoutState(context, adjustedPieces, catalog, backgroundIndex)
                }
            } else {
                // Background inicial de refúgio número 4 (conforme especificado)
                backgroundIndex = 4
                if (tutorialStep.isCompleted) {
                    val defaultMona = PlacedArtPiece(
                        artPieceId = "monalisa_art",
                        positionX = 180f,
                        positionY = 25f,
                        widthDp = 195f,
                        heightDp = 240f,
                        zone = PlacementZone.WALL
                    )
                    placedPieces = listOf(defaultMona)
                    catalog = catalog.map { piece ->
                        val isPlaced = piece.id == defaultMona.artPieceId
                        piece.copy(isPlaced = isPlaced)
                    }
                } else {
                    // No tutorial o refúgio inicia limpo para aprender a posicionar o presente do Marcel
                    placedPieces = emptyList()
                }
                saveHideoutState(context, placedPieces, catalog, backgroundIndex)
            }
        }

        if (activeTab == MainMenuTab.HIDEOUT) {
            // --- CENTER AREA: Interactive Hideout Room with horizontal scroll extending behind bottom HUD ---
            Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(roomAreaHeight)
                .offset(y = topAreaHeight)
                .horizontalScroll(roomScrollState)
                .zIndex(1f)
        ) {
            RoomBackgroundMainMenu(
                widthDp = roomWidthDp,
                heightDp = roomAreaHeight,
                backgroundIndex = backgroundIndex,
                isPlacingMode = draggingPieceId != null
            )

            // Ordenação estrita de camadas Z:
            // 1. Pinturas (WALL) são renderizadas primeiro, mais ao fundo na parede.
            // 2. Esculturas e Relíquias (FLOOR) são renderizadas na camada frontal sobreposta à parede.
            val sortedPlacedPieces = remember(placedPieces) {
                placedPieces.sortedBy { if (it.zone == PlacementZone.WALL) 0 else 1 }
            }

            for (placed in sortedPlacedPieces) {
                // Se a obra estiver sendo movimentada, esconde a fixa original
                if (draggingPieceId == placed.artPieceId) continue

                val art = catalog.firstOrNull { it.id == placed.artPieceId } ?: continue
                val isSelected = selectedPieceId == placed.artPieceId

                Box(
                    modifier = Modifier
                        .offset(x = placed.positionX.dp, y = placed.positionY.dp)
                        .pointerInput(placed.artPieceId) {
                            detectTapGestures(
                                onTap = {
                                    selectedPieceId = if (isSelected) null else placed.artPieceId
                                },
                                onLongPress = {
                                    // Apertar e segurar ativa diretamente o modo mover
                                    selectedPieceId = placed.artPieceId
                                    draggingPieceId = placed.artPieceId
                                    dragPosition = Offset(placed.positionX, placed.positionY)
                                    isDragValid = true
                                }
                            )
                        }
                ) {
                    ArtPieceComposable(
                        artPiece = art,
                        isSelected = isSelected,
                        isMoving = false
                    )

                    if (isSelected && draggingPieceId == null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-36).dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xF02D2218),
                            border = BorderStroke(1.2.dp, Color(0xFFE5B83B)),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        draggingPieceId = placed.artPieceId
                                        dragPosition = Offset(placed.positionX, placed.positionY)
                                        isDragValid = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.OpenWith, contentDescription = "Mover", tint = Color(0xFFFFD573), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Mover", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                VerticalDivider(modifier = Modifier.height(14.dp), color = Color(0x44FFFFFF))
                                TextButton(
                                    onClick = {
                                        placedPieces = placedPieces.filter { it.artPieceId != placed.artPieceId }
                                        catalog = catalog.map { c -> if (c.id == placed.artPieceId) c.copy(isPlaced = false) else c }
                                        saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                                        selectedPieceId = null
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Inventory, contentDescription = "Guardar", tint = PastelCoral, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Guardar", color = PastelCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // GHOST PREVIEW / Movimentação no MainMenu
            if (draggingPieceId != null) {
                val piece = catalog.firstOrNull { it.id == draggingPieceId }
                if (piece != null) {
                    val previewX = dragPosition.x.coerceIn(10f, roomWidthDp.value - piece.widthDp - 10f)
                    // Relíquias e esculturas só se movimentam rigorosamente na horizontal (Y travado na linha do piso)
                    val previewY = if (piece.zone == PlacementZone.WALL) {
                        dragPosition.y.coerceIn(wallZoneTop - 5f, wallZoneBottom - piece.heightDp + 15f)
                    } else {
                        floorTargetBottomY - piece.heightDp
                    }
                    val isValid = validatePlacement(piece, previewX, previewY, ignoreId = draggingPieceId)
                    isDragValid = isValid

                    Box(
                        modifier = Modifier
                            .offset(x = previewX.dp, y = previewY.dp)
                            .size(piece.widthDp.dp, piece.heightDp.dp)
                            .border(
                                width = 2.5.dp,
                                color = if (isValid) Color(0xFF38B27A) else Color(0xFFE84A5F),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (isValid) Color(0x2238B27A) else Color(0x33E84A5F),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .pointerInput(draggingPieceId) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragPosition = Offset(
                                            x = dragPosition.x + dragAmount.x / density.density,
                                            y = if (piece.zone == PlacementZone.WALL) {
                                                dragPosition.y + dragAmount.y / density.density
                                            } else {
                                                floorTargetBottomY - piece.heightDp
                                            }
                                        )
                                    },
                                    onDragEnd = {}
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        ArtPieceComposable(
                            artPiece = piece,
                            isSelected = true,
                            isMoving = true,
                            modifier = Modifier.alpha(0.92f)
                        )

                        // Confirmação / Cancelamento (para peças de chão, fica acima da peça para não cobrir a HUD)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xF21C1610),
                            border = BorderStroke(1.2.dp, Color(0xFFE5B83B)),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .align(if (piece.zone == PlacementZone.FLOOR) Alignment.TopCenter else Alignment.BottomCenter)
                                .offset(y = if (piece.zone == PlacementZone.FLOOR) (-42).dp else 42.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isTutorialFixarHighlight = (tutorialStep == TutorialStep.STEP_9_GUIDE_CONFIRM_PLACEMENT)
                                Button(
                                    onClick = {
                                        if (isValid) {
                                            val newPlaced = PlacedArtPiece(
                                                artPieceId = piece.id,
                                                positionX = previewX,
                                                positionY = previewY,
                                                widthDp = piece.widthDp,
                                                heightDp = piece.heightDp,
                                                zone = piece.zone
                                            )
                                            placedPieces = placedPieces.filter { it.artPieceId != piece.id } + newPlaced
                                            catalog = catalog.map { c -> if (c.id == piece.id) c.copy(isPlaced = true) else c }
                                            saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                                            selectedPieceId = null
                                            draggingPieceId = null

                                            if (tutorialStep == TutorialStep.STEP_9_GUIDE_CONFIRM_PLACEMENT) {
                                                tutorialStep = TutorialStep.STEP_10_MARCEL_FAREWELL
                                                TutorialManager.setStep(context, tutorialStep)
                                            }
                                        }
                                    },
                                    enabled = isValid,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTutorialFixarHighlight) Color(0xFF01875F) else Color(0xFF288C52)
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .height(26.dp)
                                        .then(
                                            if (isTutorialFixarHighlight) Modifier.border(2.dp, Color(0xFFFFD573), RoundedCornerShape(6.dp))
                                            else Modifier
                                        )
                                ) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Fixar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                TextButton(
                                    onClick = { draggingPieceId = null },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = PastelCoral, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Cancelar", fontSize = 10.sp, color = PastelCoral)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- FIXED HUD OVERLAY ON TOP IN Z-INDEX (Z-AXIS) ---
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.hud_menu),
            contentDescription = "HUD Overlay",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
        )

        // --- TOP LEFT: User Profile Icon + Gold Coins Resource Marker (zIndex 20f) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = screenHeight * 0.02f + 2.dp, start = 10.dp)
                .zIndex(20f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.haptics.playScrewClick()
                    showLoginModal = true
                },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("user_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil / Conta de Usuário",
                    tint = Color(0xFF5D4037),
                    modifier = Modifier.size(36.dp)
                )
            }

            TopResourceMarkerPill(
                icon = Icons.Default.MonetizationOn,
                iconTint = Color(0xFFF5B025),
                amountText = formatCoinAmount(state.coins),
                contentDescription = "Moedas de Ouro",
                testTagStr = "gold_coins_marker",
                onClick = {
                    viewModel.haptics.playScrewClick()
                    viewModel.addCoins(100)
                }
            )
        }

        // --- TOP CENTER: LOGO (in top 10% HUD area, moved up) (zIndex 20f) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topAreaHeight)
                .zIndex(20f),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logotipo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(y = topAreaHeight * 0.15f)
                    .requiredHeight(topAreaHeight * 1.35f)
            )
        }

        // --- TOP RIGHT: Keys Resource Marker + Settings Icon (zIndex 20f) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = screenHeight * 0.02f + 2.dp, end = 10.dp)
                .zIndex(20f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TopResourceMarkerPill(
                icon = Icons.Default.VpnKey,
                iconTint = Color(0xFFE5B83B),
                amountText = "${state.keys}",
                contentDescription = "Chaves",
                testTagStr = "keys_marker",
                onClick = {
                    viewModel.haptics.playScrewClick()
                    viewModel.addKeys(5)
                }
            )

            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = if (!tutorialStep.isCompleted) Color(0xFF9E9E9E) else Color(0xFF5D4037),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // --- BOTTOM PART: Reorganized Action Dock Bar (zIndex 20f) ---
        val infiniteTransition = rememberInfiniteTransition(label = "tutorialDockPulse")
        val dockPulseScale by infiniteTransition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(750, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dockPulseScale"
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomAreaHeight)
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .padding(bottom = 74.dp)
                .zIndex(20f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Action Button: Inventário de Obras (com Ícone de BUSTO)
                val isInventoryStep = (tutorialStep == TutorialStep.STEP_9_GUIDE_OPEN_INVENTORY)
                Box {
                    JuicySquareButton(
                        onClick = {
                            if (isInventoryStep) {
                                viewModel.haptics.playScrewClick()
                                showArtworksModal = true
                                tutorialStep = TutorialStep.STEP_9_GUIDE_SELECT_GIFT_ART
                                TutorialManager.setStep(context, tutorialStep)
                            } else if (tutorialStep.isCompleted) {
                                viewModel.haptics.playScrewClick()
                                showArtworksModal = true
                            }
                        },
                        size = 54.dp,
                        modifier = if (isInventoryStep) {
                            Modifier
                                .scale(dockPulseScale)
                                .border(3.dp, Color(0xFFFFD573), RoundedCornerShape(16.dp))
                        } else Modifier,
                        testTag = "artworks_inventory_bottom_button"
                    ) {
                        BustIcon(
                            size = 28.dp,
                            tint = if (isInventoryStep) Color(0xFFB45309) else Color(0xFF1B281E)
                        )
                    }

                    if (isInventoryStep) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-36).dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF261D16),
                            border = BorderStroke(1.dp, Color(0xFFFFD573))
                        ) {
                            Text(
                                text = "Acervo! ☝️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD573),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Center Stack: Botões Principais de Jogo
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. "Jogar Sagas" Button (Destacado na Etapa 3 do Tutorial)
                    val isJogarSagasHighlighted = (tutorialStep == TutorialStep.STEP_3_HIGHLIGHT_JOGAR_SAGAS)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        JuicyRectangularButton(
                            onClick = {
                                if (isJogarSagasHighlighted) {
                                    viewModel.haptics.playScrewClick()
                                    showTutorialSagaModal = true
                                    tutorialStep = TutorialStep.STEP_4_HIGHLIGHT_SAGA_1
                                    TutorialManager.setStep(context, tutorialStep)
                                } else if (tutorialStep.isCompleted) {
                                    viewModel.goToSagaSelection()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .then(
                                    if (isJogarSagasHighlighted) {
                                        Modifier
                                            .scale(dockPulseScale)
                                            .border(3.dp, Color(0xFFFFD573), RoundedCornerShape(16.dp))
                                    } else Modifier
                                ),
                            testTag = "jogar_sagas_button"
                        ) {
                            Text(
                                text = "JOGAR SAGAS",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isJogarSagasHighlighted) Color(0xFF8D6218) else Color(0xFF1B281E),
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (isJogarSagasHighlighted) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-34).dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF261D16),
                                border = BorderStroke(1.dp, Color(0xFFFFD573))
                            ) {
                                Text(
                                    text = "Toque em Jogar Sagas! ☝️",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD573),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // 2. "Jogo Rápido" Button (Desativado no tutorial até explorar livremente)
                    JuicyRectangularButton(
                        onClick = {
                            if (tutorialStep.isCompleted) {
                                viewModel.showQuickPlayModal(true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        tint = if (!tutorialStep.isCompleted) Color(0xFFBCAAA4) else Color(0xFF8D7B68),
                        testTag = "jogo_rapido_button"
                    ) {
                        Text(
                            text = "JOGO RÁPIDO",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (!tutorialStep.isCompleted) Color(0xFF757575) else Color(0xFF1B281E),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Action Button: Administração do Esconderijo (Desativado no tutorial)
                JuicySquareButton(
                    onClick = {
                        if (tutorialStep.isCompleted) {
                            viewModel.haptics.playScrewClick()
                            showHideoutAdminModal = true
                        }
                    },
                    size = 54.dp,
                    tint = if (!tutorialStep.isCompleted) Color(0xFFBCAAA4) else Color(0xFF8D7B68),
                    testTag = "hideout_admin_bottom_button"
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Administração do Esconderijo",
                        tint = if (!tutorialStep.isCompleted) Color(0xFF757575) else Color(0xFF1B281E),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    } else {
        // Other 4 Tabs Content:
        when (activeTab) {
            MainMenuTab.STORE -> {
                LojaTabScreen(viewModel = viewModel, state = state)
            }
            MainMenuTab.VAULT -> {
                CofreTabScreen(viewModel = viewModel, state = state)
            }
            MainMenuTab.BARRACKS -> {
                QuartelScreen(viewModel = viewModel, state = state)
            }
            MainMenuTab.EVENTS,
            MainMenuTab.HIDEOUT -> Unit
        }
    }

        // Settings Dialog
        if (showSettingsDialog) {
            GameSettingsDialog(
                state = state,
                viewModel = viewModel,
                onDismiss = { showSettingsDialog = false }
            )
        }

        // User Profile & Account Dialog
        if (showLoginModal) {
            UserProfileDialog(
                state = state,
                viewModel = viewModel,
                authViewModel = authViewModel,
                onDismiss = { showLoginModal = false }
            )
        }

        // Hideout Administration Dialog (Mudar Background e Comprar Defesas)
        if (showHideoutAdminModal) {
            HideoutAdminDialog(
                viewModel = viewModel,
                currentBackgroundIndex = backgroundIndex,
                onSelectBackground = { newBgIndex ->
                    backgroundIndex = newBgIndex
                    saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                },
                onDismiss = { showHideoutAdminModal = false }
            )
        }

        // Artworks Inventory Dialog (Modal com filtro integrado no topo e controle de capacidade)
        if (showArtworksModal) {
            ArtworksInventoryDialog(
                catalog = catalog,
                placedPieces = placedPieces,
                highlightGiftPieceId = if (!tutorialStep.isCompleted) "homensjogandocartas_art" else null,
                onPlaceArt = { piece ->
                    if (!piece.isPlaced) {
                        // Limites estritos: máx. 5 no total, máx. 4 pinturas, máx. 4 esculturas/relíquias
                        val currentWallCount = placedPieces.count { it.zone == PlacementZone.WALL }
                        val currentFloorCount = placedPieces.count { it.zone == PlacementZone.FLOOR }
                        val canPlace = when (piece.zone) {
                            PlacementZone.WALL -> placedPieces.size < 5 && currentWallCount < 4
                            PlacementZone.FLOOR -> placedPieces.size < 5 && currentFloorCount < 4
                        }
                        if (canPlace) {
                            // Encontra posição livre garantindo que NUNCA renderize em cima de outra obra do mesmo tipo/zona
                            val safeSpot = findSafeSpawnPosition(piece)
                            if (safeSpot != null) {
                                val (safeX, safeY) = safeSpot
                                val newPlaced = PlacedArtPiece(
                                    artPieceId = piece.id,
                                    positionX = safeX,
                                    positionY = safeY,
                                    widthDp = piece.widthDp,
                                    heightDp = piece.heightDp,
                                    zone = piece.zone
                                )
                                placedPieces = placedPieces + newPlaced
                                catalog = catalog.map { c -> if (c.id == piece.id) c.copy(isPlaced = true) else c }
                                saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                                showArtworksModal = false

                                // Na etapa 9 do tutorial, ativa o modo de ajuste e foco no botão "Fixar"
                                selectedPieceId = piece.id
                                draggingPieceId = piece.id
                                dragPosition = Offset(safeX, safeY)
                                isDragValid = true
                                if (tutorialStep == TutorialStep.STEP_9_GUIDE_SELECT_GIFT_ART) {
                                    tutorialStep = TutorialStep.STEP_9_GUIDE_CONFIRM_PLACEMENT
                                    TutorialManager.setStep(context, tutorialStep)
                                }
                            }
                        }
                    }
                },
                onRemoveArt = { piece ->
                    placedPieces = placedPieces.filter { it.artPieceId != piece.id }
                    catalog = catalog.map { c -> if (c.id == piece.id) c.copy(isPlaced = false) else c }
                    saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                },
                onDismiss = { showArtworksModal = false }
            )
        }

        if (state.showQuickPlayModal) {
            QuickPlayDifficultyModal(
                onSelectDifficulty = { difficulty ->
                    viewModel.startQuickPlayGame(difficulty)
                },
                onDismiss = { viewModel.showQuickPlayModal(false) }
            )
        }

        // Rewarded Ad Simulation Dialog (para testes e recompensas quando acionado no Menu Inicial ou na Loja)
        if (state.showAdSimulationDialog) {
            RewardedAdSimulationDialog(
                rewardType = state.adSimulationRewardType ?: "",
                onRewardCompleted = { viewModel.completeRewardedAd() },
                onDismiss = { viewModel.dismissAdSimulation() }
            )
        }

        // --- CASUAL 5-TAB BOTTOM NAVIGATION BAR (zIndex 40f) ---
        CasualBottomTabBar(
            selectedTab = activeTab,
            onSelectTab = { tab ->
                // No tutorial, o jogador fica focado no Refúgio até completar as 10 etapas
                if (tutorialStep.isCompleted && tab != MainMenuTab.EVENTS) {
                    viewModel.haptics.playScrewClick()
                    activeTab = tab
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .zIndex(40f)
        )

        // =========================================================================
        // === CAMADAS E OVERLAYS DO TUTORIAL OFICIAL (ETAPAS 1 A 10) ===
        // =========================================================================

        // Etapa 1: Interceptador de toques durante os primeiros segundos no refúgio
        if (tutorialStep == TutorialStep.STEP_1_INITIAL_FREEZE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                    .zIndex(99f)
            )
        }

        // Etapa 2: Diálogo inicial do Marcel no Menu Principal (tela suavemente escurecida)
        if (tutorialStep == TutorialStep.STEP_2_MARCEL_DIALOGUE) {
            val marcelOpeningSpeech = listOf(
                "Olá! Seja muito bem-vindo ao seu novo Esconderijo secreto!",
                "Como pode ver, este lugar ainda está precisando de uma boa reforma...",
                "Mas não se preocupe! Nós dois vamos resgatar as maiores obras de arte do mundo e decorar nosso refúgio!",
                "Para isso, precisamos invadir os cofres dos grandes magnatas e desparafusar as placas de proteção.",
                "Venha comigo! Toque no botão de Sagas para iniciarmos a nossa primeira operação!"
            )
            TutorialMarcelDialogue(
                dialogueTexts = marcelOpeningSpeech,
                currentTextIndex = marcelOpeningIndex,
                onNextText = {
                    viewModel.haptics.playScrewClick()
                    if (marcelOpeningIndex < marcelOpeningSpeech.size - 1) {
                        marcelOpeningIndex++
                    } else {
                        tutorialStep = TutorialStep.STEP_3_HIGHLIGHT_JOGAR_SAGAS
                        TutorialManager.setStep(context, tutorialStep)
                    }
                },
                modifier = Modifier.zIndex(80f)
            )
        }

        // Etapas 4, 5, 6 e 7: Modal de Fluxo de Saga, Fase 1, Cartinha do Poddle e Recompensa em Ouro
        if (showTutorialSagaModal) {
            TutorialSagaFlowModal(
                viewModel = viewModel,
                onCompleteTutorialLevel1 = {
                    showTutorialSagaModal = false
                    showTutorialGiftModal = true
                    tutorialStep = TutorialStep.STEP_8_GIFT_REVEAL
                    TutorialManager.setStep(context, tutorialStep)
                },
                onDismiss = {
                    // Mantém modal aberto durante a condução obrigatória do tutorial
                }
            )
        }

        // Etapa 8: Marcel dá uma obra fora das sagas de presente (Os Jogadores de Cartas)
        if (showTutorialGiftModal) {
            TutorialGiftRevealModal(
                onAcceptGift = {
                    viewModel.haptics.playScrewClick()
                    showTutorialGiftModal = false
                    // Destrava a obra "Os Jogadores de Cartas" no catálogo
                    catalog = catalog.map { c ->
                        if (c.id == "homensjogandocartas_art") c.copy(isUnlocked = true) else c
                    }
                    saveHideoutState(context, placedPieces, catalog, backgroundIndex)
                    tutorialStep = TutorialStep.STEP_9_GUIDE_OPEN_INVENTORY
                    TutorialManager.setStep(context, tutorialStep)
                }
            )
        }

        // Etapa 9C: Indicador do Marcel auxiliando a fixar a nova obra na parede
        if (tutorialStep == TutorialStep.STEP_9_GUIDE_CONFIRM_PLACEMENT && draggingPieceId != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = screenHeight * 0.12f)
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .zIndex(85f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF261D16),
                border = BorderStroke(1.5.dp, Color(0xFFFFD573))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Marcel: \"Excelente posição! Agora toque no botão verde 'Fixar' para prendê-la na parede!\"",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Etapa 10: Marcel diz que isso é tudo por hora e que já podemos explorar sozinhos. Início do jogo normal!
        if (tutorialStep == TutorialStep.STEP_10_MARCEL_FAREWELL) {
            val marcelFarewellSpeech = listOf(
                "Ficou sensacional! Veja só como o nosso refúgio já tem cara de uma verdadeira base secreta!",
                "Isso é tudo por hora! Agora que você já conhece o básico, pode explorar tudo livremente.",
                "Vá em frente, arrombe novos cofres, colecione relíquias e construa a sua lenda!"
            )
            TutorialMarcelDialogue(
                dialogueTexts = marcelFarewellSpeech,
                currentTextIndex = marcelFarewellIndex,
                onNextText = {
                    viewModel.haptics.playScrewClick()
                    if (marcelFarewellIndex < marcelFarewellSpeech.size - 1) {
                        marcelFarewellIndex++
                    } else {
                        tutorialStep = TutorialStep.STEP_COMPLETED
                        TutorialManager.setTutorialCompleted(context, true)
                    }
                },
                modifier = Modifier.zIndex(80f)
            )
        }
    }
}



private fun saveHideoutState(context: android.content.Context, placedPieces: List<PlacedArtPiece>, catalog: List<ArtPiece>, bgIndex: Int = 4) {
    val unlockedIds = catalog.filter { it.isUnlocked }.map { it.id }.toSet()
    HideoutSaveManager.saveHideout(context, placedPieces, unlockedIds, bgIndex)
}

@Composable
private fun RoomBackgroundMainMenu(
    widthDp: androidx.compose.ui.unit.Dp,
    heightDp: androidx.compose.ui.unit.Dp,
    backgroundIndex: Int = 4,
    isPlacingMode: Boolean = false
) {
    val bgRes = HideoutSaveManager.getBackgroundDrawable(backgroundIndex)
    Box(
        modifier = Modifier
            .size(width = widthDp, height = heightDp)
            .background(Color(0xFFF3ECE4))
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(bgRes),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

