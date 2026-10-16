package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.hideout.ArtPiece
import com.example.hideout.ArtRarity
import com.example.hideout.ArtThumbnailComposable
import com.example.hideout.ArtType
import com.example.hideout.PlacedArtPiece
import com.example.hideout.PlacementZone
import com.example.ui.theme.*

/**
 * Grid-based Inventory inside the game's stylized custom modal frame (R.drawable.modalcomumgrande).
 *
 * Implements the classic game item grid:
 * - Fits cleanly within the custom modal frame without scroll conflict
 * - Clean category tabs: "Tudo", "🖼️ Pinturas", "🗿 Esculturas", "🏺 Relíquias"
 * - Compact room capacity pill (X/5)
 * - 4-column item slot grid (like an RPG inventory)
 * - Selected item bottom inspection tray with:
 *     - Large thumbnail
 *     - Title, rarity & saga origin
 *     - Action button ("Exibir na Sala", "Já Exposta", "Sala Cheia", etc.)
 *     - Inspect button to open detailed full-screen inspection dialog
 */
@Composable
fun ArtworksInventoryDialog(
    catalog: List<ArtPiece>,
    placedPieces: List<PlacedArtPiece> = emptyList(),
    highlightGiftPieceId: String? = null,
    onPlaceArt: ((ArtPiece) -> Unit)? = null,
    onRemoveArt: ((ArtPiece) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var selectedTypeFilter by remember { mutableIntStateOf(0) } // 0 = Tudo, 1 = Pinturas, 2 = Esculturas, 3 = Relíquias
    var inspectingPieceDialog by remember { mutableStateOf<ArtPiece?>(null) }

    val unlockedCount = remember(catalog) { catalog.count { it.isUnlocked } }
    val totalPlaced = placedPieces.size
    val paintingsPlaced = remember(placedPieces) { placedPieces.count { it.zone == PlacementZone.WALL } }
    val floorPlaced = remember(placedPieces) { placedPieces.count { it.zone == PlacementZone.FLOOR } }

    val typeFilters = listOf(
        "Tudo",
        "🖼️ Paredes",
        "🗿 Chão",
        "🏺 Relíquias"
    )

    val filteredList = remember(catalog, selectedTypeFilter) {
        catalog.filter { piece ->
            when (selectedTypeFilter) {
                0 -> true
                1 -> piece.type == ArtType.PAINTING
                2 -> piece.type == ArtType.SCULPTURE
                3 -> piece.type == ArtType.RELIC
                else -> true
            }
        }
    }

    // Currently focused item in the inventory tray (auto-selects gift piece if in tutorial)
    var focusedPieceId by remember(highlightGiftPieceId) {
        mutableStateOf(highlightGiftPieceId)
    }

    val activeSelectedPiece = remember(filteredList, focusedPieceId) {
        filteredList.firstOrNull { it.id == focusedPieceId }
            ?: filteredList.firstOrNull { it.isUnlocked }
            ?: filteredList.firstOrNull()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            // Container with exact game modal frame (R.drawable.modalcomumgrande)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .fillMaxHeight(0.92f)
                    .widthIn(max = 440.dp)
                    .clickable(enabled = false) {} // Prevent click-through
                    .testTag("artworks_inventory_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Border padding matching the graphic border of modalcomumgrande
                val hPad = maxWidth * 0.17f
                val vPadTop = maxHeight * 0.12f
                val vPadBottom = maxHeight * 0.21f

                // Modal Background Frame Graphic
                Image(
                    painter = painterResource(id = R.drawable.modalcomumgrande),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                // Interior Content (Non-conflicting layout: Fixed Top + Grid + Bottom Tray)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = hPad,
                            end = hPad,
                            top = vPadTop,
                            bottom = vPadBottom
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ==========================================
                    // 1. HEADER (Title & Room Capacity)
                    // ==========================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Acervo do Esconderijo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CozyTextPrimary
                            )
                            Text(
                                text = "$unlockedCount / ${catalog.size} resgatadas",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = CozyTextSecondary
                            )
                        }

                        // Room occupancy badge
                        val isRoomFull = totalPlaced >= 5
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isRoomFull) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            border = BorderStroke(
                                1.dp,
                                if (isRoomFull) Color(0xFFE57373) else Color(0xFF81C784)
                            )
                        ) {
                            Text(
                                text = "Sala: $totalPlaced/5",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isRoomFull) Color(0xFFC62828) else Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ==========================================
                    // 2. CATEGORY TABS
                    // ==========================================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE9DFD0), RoundedCornerShape(10.dp))
                            .padding(2.5.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        typeFilters.forEachIndexed { index, label ->
                            val isSelected = selectedTypeFilter == index
                            val bg by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF5D4037) else Color.Transparent,
                                label = "tabBg"
                            )
                            val textCol = if (isSelected) Color.White else Color(0xFF795548)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { selectedTypeFilter = index }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = textCol,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ==========================================
                    // 3. INVENTORY SLOTS GRID (4 COLUMNS)
                    // ==========================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color(0xFFEFE7DB), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFDCCFBD), RoundedCornerShape(12.dp))
                            .padding(6.dp)
                    ) {
                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhum item nesta categoria",
                                    fontSize = 11.sp,
                                    color = CozyTextSecondary
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredList, key = { it.id }) { piece ->
                                    val isFocused = activeSelectedPiece?.id == piece.id
                                    InventorySlotItem(
                                        piece = piece,
                                        isFocused = isFocused,
                                        onClick = { focusedPieceId = piece.id }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ==========================================
                    // 4. SELECTED ITEM ACTION TRAY
                    // ==========================================
                    if (activeSelectedPiece != null) {
                        InventoryFocusedDetailTray(
                            piece = activeSelectedPiece,
                            totalPlaced = totalPlaced,
                            paintingsPlaced = paintingsPlaced,
                            floorPlaced = floorPlaced,
                            onPlaceArt = onPlaceArt,
                            onRemoveArt = onRemoveArt,
                            onInspect = { inspectingPieceDialog = activeSelectedPiece }
                        )
                    }
                }

                // Modal Close Button in Top-Right
                JuicyCloseButton(
                    onClick = onDismiss,
                    size = 38.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = -(hPad * 0.55f), y = vPadTop * 0.35f),
                    testTag = "inventory_close_button"
                )
            }
        }
    }

    // Detailed Inspection Dialog on Demand
    if (inspectingPieceDialog != null) {
        val piece = inspectingPieceDialog!!
        val artNum = piece.id.toIntOrNull() ?: 1
        ArtPieceInspectionDialog(
            artPiece = piece,
            artIndex = (artNum - 1).coerceAtLeast(0) % 5,
            isUnlocked = piece.isUnlocked,
            bossLevel = piece.sagaId * 20,
            onDismiss = { inspectingPieceDialog = null }
        )
    }
}

/**
 * Individual classic RPG-style Inventory Slot.
 */
@Composable
private fun InventorySlotItem(
    piece: ArtPiece,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val isUnlocked = piece.isUnlocked
    val isPlaced = piece.isPlaced

    val slotBg = when {
        isFocused -> Color(0xFFFFF9C4) // Highlight gold
        isPlaced -> Color(0xFFE8F5E9)  // Mint tint
        isUnlocked -> Color(0xFFFAF6F0) // Warm linen
        else -> Color(0xFF3E2723)       // Dark silhouette slot
    }

    val slotBorderColor = when {
        isFocused -> Color(0xFFFFA000)
        isPlaced -> Color(0xFF81C784)
        isUnlocked -> Color(0xFFD7CCC8)
        else -> Color(0xFF4E342E)
    }

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = slotBg,
        border = BorderStroke(if (isFocused) 2.dp else 1.dp, slotBorderColor),
        shadowElevation = if (isFocused) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                ArtThumbnailComposable(
                    artPiece = piece,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    showPedestalForRelics = true
                )

                // "Na sala" badge
                if (isPlaced) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(14.dp)
                            .background(Color(0xFF2E7D32), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Trancado",
                    tint = Color(0xFFA1887F),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Bottom action tray showing details and placement action for the selected slot.
 */
@Composable
private fun InventoryFocusedDetailTray(
    piece: ArtPiece,
    totalPlaced: Int,
    paintingsPlaced: Int,
    floorPlaced: Int,
    onPlaceArt: ((ArtPiece) -> Unit)?,
    onRemoveArt: ((ArtPiece) -> Unit)?,
    onInspect: () -> Unit
) {
    val isUnlocked = piece.isUnlocked
    val isPlaced = piece.isPlaced

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAF6F0),
        border = BorderStroke(1.2.dp, Color(0xFFDCCFBD)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preview thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isUnlocked) Color.White else Color(0xFF2E1C14))
                    .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(8.dp))
                    .clickable { onInspect() },
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    ArtThumbnailComposable(
                        artPiece = piece,
                        modifier = Modifier.padding(2.dp),
                        showPedestalForRelics = true
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFA1887F),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Metadata Column
            Column(modifier = Modifier.weight(1f)) {
                if (isUnlocked) {
                    Text(
                        text = piece.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = CozyTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = piece.rarity.label.uppercase(),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(piece.rarity.badgeColorHex)
                        )
                    }
                } else {
                    Text(
                        text = "Silhueta Misteriosa",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = if (piece.sagaId > 0) "Vença o Boss da Saga ${piece.sagaId}" else "Recompensa Especial",
                        fontSize = 8.5.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Action Button
            if (isUnlocked) {
                if (isPlaced) {
                    if (onRemoveArt != null) {
                        JuicyRectangularButton(
                            onClick = { onRemoveArt(piece) },
                            modifier = Modifier.height(34.dp),
                            tint = Color(0xFFE57373),
                            testTag = "remove_artwork_btn"
                        ) {
                            Text(
                                text = "Retirar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                } else if (onPlaceArt != null) {
                    val canPlace = when (piece.zone) {
                        PlacementZone.WALL -> totalPlaced < 5 && paintingsPlaced < 4
                        PlacementZone.FLOOR -> totalPlaced < 5 && floorPlaced < 4
                    }

                    val disabledReason = when {
                        totalPlaced >= 5 -> "Sala Cheia"
                        piece.zone == PlacementZone.WALL && paintingsPlaced >= 4 -> "Máx. Parede"
                        piece.zone == PlacementZone.FLOOR && floorPlaced >= 4 -> "Máx. Chão"
                        else -> null
                    }

                    if (canPlace) {
                        JuicyRectangularButton(
                            onClick = { onPlaceArt(piece) },
                            modifier = Modifier.height(34.dp),
                            testTag = "place_artwork_btn"
                        ) {
                            Text(
                                text = "Exibir",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B281E)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECE6DD),
                            border = BorderStroke(1.dp, Color(0xFFD0C5B4))
                        ) {
                            Text(
                                text = disabledReason ?: "Limite",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D7F71),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFEBE9),
                    border = BorderStroke(1.dp, Color(0xFFD7CCC8))
                ) {
                    Text(
                        text = "Trancada",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D6E63),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
