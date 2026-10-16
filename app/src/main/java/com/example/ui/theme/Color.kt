package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =========================================================================
// COZY PASTEL PALETTE DEFINITIONS (Soft Blues, Pale Pinks, Warm Creams & Mints)
// =========================================================================

// Warm Creams & Linen Canvas
val CreamLinenBackground = Color(0xFFF9F6F0)        // Warm soft oatmeal canvas
val CreamSandBackground = Color(0xFFF3ECE1)         // Warm cozy beige alternative
val CreamCloudSurface = Color(0xFFFFFFFF)           // Pure cloud card surface
val CreamSubtleSurface = Color(0xFFF6F2EA)          // Gentle warm cream container
val CreamElevatedSurface = Color(0xFFFAF7F2)        // Soft lifted card surface
val CreamBoardMat = Color(0xFFEFE8DB)               // Tactile cozy desk craft mat
val CreamDockTray = Color(0xFFEADBCE)               // Warm rounded wooden craft dock
val CreamDockSlotBg = Color(0xFFFDFBF7)             // Inset slot background

// Cozy Espresso Typography (Gentle yet high-contrast)
val CozyEspressoText = Color(0xFF352F2D)            // Primary readable dark warm text
val CozyCappuccinoText = Color(0xFF7A6F68)          // Secondary warm subtitle text
val CozyTaupeMutedText = Color(0xFFA89F97)          // Subtle muted placeholder text

// Soft Blues (Powder Sky, Cloud Baby Blue)
val PastelSoftBlue = Color(0xFFC7CEEA)              // Primary soft sky pastel
val PastelSoftBlueDark = Color(0xFF7986CB)          // Deep soft blue accent
val PastelSoftBlueLight = Color(0xFFEEF2FF)         // Pale blue tinted highlight
val PastelSoftBlueBg = Color(0xFFF4F6FF)            // Pale blue chip/card container

// Pale Pinks (Strawberry Milkshake, Soft Rose)
val PalePinkPastel = Color(0xFFFF9AA2)              // Sweet pastel strawberry pink
val PalePinkDark = Color(0xFFE57373)                // Rich rose accent
val PalePinkLight = Color(0xFFFFE6E8)               // Milky pink highlight
val PalePinkBg = Color(0xFFFFF0F1)                  // Delicate pink pill container

// Warm Creams & Honey Butter
val WarmVanillaCream = Color(0xFFFFEAA7)            // Gentle pastel butter/vanilla
val WarmVanillaDark = Color(0xFFE5B83B)             // Rich warm amber accent
val WarmVanillaLight = Color(0xFFFFFBEA)            // Pale cream highlight
val WarmVanillaBg = Color(0xFFFFFDF5)               // Soft butter chip container

// Soft Mints & Sage (Refreshing Nature Tone)
val SoftMintPastel = Color(0xFFB5EAD7)              // Calm pastel mint
val SoftMintDark = Color(0xFF57B894)                // Forest mint accent
val SoftMintLight = Color(0xFFE8FAF3)               // Pale mint highlight
val SoftMintBg = Color(0xFFF0FDF8)                  // Soft mint chip container

// Soft Lilacs & Lavender (Serene Relaxing Tone)
val SoftLilacPastel = Color(0xFFE2D4F0)             // Peaceful lavender pastel
val SoftLilacDark = Color(0xFF9E77C4)               // Lilac purple accent
val SoftLilacLight = Color(0xFFF7F2FC)              // Pale lilac highlight
val SoftLilacBg = Color(0xFFFAF6FE)                 // Soft lilac chip container

// Soft Peaches & Apricot (Warm Radiant Tone)
val SoftPeachPastel = Color(0xFFFFDAC1)             // Cozy apricot peach pastel
val SoftPeachDark = Color(0xFFE89065)               // Warm terracotta accent
val SoftPeachLight = Color(0xFFFFF4EC)              // Pale peach highlight
val SoftPeachBg = Color(0xFFFFF7F2)                 // Soft peach chip container

// Cozy Material & Skin Accents
val NordicOakWood = Color(0xFFE2C9A5)
val NordicBirchWood = Color(0xFFF0DEC2)
val NordicDarkWood = Color(0xFFA67C52)

// Dedicated High-End Cozy Plate Tones (Earthy, Warm Pastels, Muted Ceramic & Biscuit)
// Specially calibrated with rich, vibrant contrast against the warm linen board tray and distinct from screws!
val PlateCreamBiscuit = Color(0xFFC89E70)       // Carvalho/Bétula dourada quente (visivelmente amadeirada)
val PlateSageMuted = Color(0xFF4C7B67)          // Sálvia artesanal rica e elegante
val PlateWarmAlmond = Color(0xFFD46848)         // Terracota acolhedora vibrante
val PlatePowderPeriwinkle = Color(0xFF4C729B)   // Cerâmica Azul Ardósia profunda
val PlateBlushLinen = Color(0xFFA85775)         // Rosa Queimado / Framboesa suave
val PlateDustyLilac = Color(0xFF755E94)         // Lavanda artesanal profunda
val PlateWarmClay = Color(0xFFC78C27)           // Mostarda / Âmbar natural rico
val PlatePaleOatmeal = Color(0xFF5D7E50)        // Oliva Musgo sofisticada
val PlateRichWalnut = Color(0xFF825232)         // Nogueira nobre acolhedora
val PlateCleanIvory = Color(0xFFFFFBF2)         // Porcelana Marfim luminosa com contorno nítido

val PorcelainLightGlaze = Color(0xFFF8FAFC)
val PorcelainBlueGlaze = Color(0xFFBAE6FD)
val PorcelainAccentGlaze = Color(0xFF38BDF8)

val WoolCreamFelt = Color(0xFFFDFBF7)
val WoolBorderFelt = Color(0xFFE8DCC9)

// Theme Aliases & Token Mappings
val CozyBackground = CreamLinenBackground
val CozyBackgroundAlt = CreamSandBackground
val CozySurface = CreamCloudSurface
val CozySurfaceSubtle = CreamSubtleSurface
val CozySurfaceElevated = CreamElevatedSurface
val CozyBoardMat = Color(0xFFE8DFD1)            // Bandeja de corte artesanal aconchegante (Warm Cozy Linen Mat)
val CozyTextPrimary = CozyEspressoText
val CozyTextSecondary = CozyCappuccinoText
val CozyTextMuted = CozyTaupeMutedText

val PastelStrawberry = PalePinkPastel
val PastelStrawberryDark = PalePinkDark
val PastelStrawberryLight = PalePinkLight
val PastelStrawberryBg = PalePinkBg

val PastelPowderSky = PastelSoftBlue
val PastelPowderSkyDark = PastelSoftBlueDark
val PastelPowderSkyLight = PastelSoftBlueLight
val PastelPowderSkyBg = PastelSoftBlueBg

val PastelVanilla = WarmVanillaCream
val PastelVanillaDark = WarmVanillaDark
val PastelVanillaLight = WarmVanillaLight
val PastelVanillaBg = WarmVanillaBg

val PastelMint = SoftMintPastel
val PastelMintDark = SoftMintDark
val PastelMintLight = SoftMintLight
val PastelMintBg = SoftMintBg

val PastelLilac = SoftLilacPastel
val PastelLilacDark = SoftLilacDark
val PastelLilacLight = SoftLilacLight
val PastelLilacBg = SoftLilacBg

val PastelPeach = SoftPeachPastel
val PastelPeachDark = SoftPeachDark
val PastelPeachLight = SoftPeachLight
val PastelPeachBg = SoftPeachBg

val CozyOakWood = NordicOakWood
val CozyBirchWood = NordicBirchWood
val CozyDarkWood = NordicDarkWood

val CozyPorcelainLight = PorcelainLightGlaze
val CozyPorcelainBlue = PorcelainBlueGlaze
val CozyPorcelainAccent = PorcelainAccentGlaze

val CozyWoolCream = WoolCreamFelt
val CozyWoolFelt = WoolBorderFelt

val DockTrayCozy = CreamDockTray
val DockSlotCozyBg = CreamDockSlotBg
val DockSlotActiveCozy = SoftMintDark
val DockSlotBorderCozy = Color(0xFFDFD1BF)

val DarkBackground = CozyBackground
val SurfaceDark = CozySurface
val SurfaceElevated = CozySurfaceElevated
val StudioBackground = CozyBackground
val StudioCardBg = CozySurface
val StudioCardElevated = CozySurfaceElevated
val StudioBorder = Color.Transparent

val PastelSalmon = PastelStrawberry
val PastelSalmonDark = PastelStrawberryDark
val PastelSalmonLight = PastelStrawberryLight

val PastelTeal = PastelPowderSky
val PastelTealDark = PastelPowderSkyDark
val PastelTealLight = PastelPowderSkyLight

val PastelSage = PastelMint
val PastelSageDark = PastelMintDark
val PastelSageLight = PastelMintLight

val PastelButter = PastelVanilla
val PastelButterDark = PastelVanillaDark
val PastelButterLight = PastelVanillaLight

val PastelLavender = PastelLilac
val PastelLavenderDark = PastelLilacDark
val PastelLavenderLight = PastelLilacLight

val PastelCoral = PastelPeach
val PastelCoralDark = PastelPeachDark
val PastelCoralLight = PastelPeachLight

val PolishedMapleWood = CozyOakWood
val PolishedBirchWood = CozyBirchWood
val DarkWalnutWood = CozyDarkWood

val ArcticIceLight = CozyPorcelainLight
val ArcticIcePrimary = CozyPorcelainBlue
val ArcticIceDeep = CozyPorcelainAccent

val WoolWarmCream = CozyWoolCream
val WoolFeltBorder = CozyWoolFelt

val DockTrayWood = DockTrayCozy
val DockSlotWoodBorder = DockSlotBorderCozy
val DockSlotMinimalBorder = Color.Transparent

val GameRed = PastelStrawberry
val GameRedDark = PastelStrawberryDark
val GameBlue = PastelPowderSkyDark
val GameBlueDark = Color(0xFF4C5D99)
val GameGreen = PastelMintDark
val GameGreenDark = Color(0xFF388E6C)
val GameYellow = PastelVanillaDark
val GameYellowDark = Color(0xFFC4931A)
val GamePurple = PastelLilacDark
val GamePurpleDark = Color(0xFF774C9D)
val GameOrange = PastelPeachDark
val GameOrangeDark = Color(0xFFC76435)

val ScrewGold = Color(0xFFE5B83B)
val ScrewGoldDark = Color(0xFFB8860B)
val ScrewSilver = Color(0xFFE8E4DE)
val DockSlotBg = DockSlotCozyBg
val DockSlotBorder = Color.Transparent
val DockSlotActive = PastelMintDark

val CardBorderCozy = Color(0x18000000)
val PastelSoftPeach = SoftPeachPastel
val PeachBorderSoft = Color(0x40E89065)
val PeachAccentColor = SoftPeachDark
val PastelSoftLilac = SoftLilacPastel

val Purple80 = PastelLilac
val PurpleGrey80 = PastelPowderSky
val Pink80 = PastelStrawberry
val Purple40 = PastelLilacDark
val PurpleGrey40 = PastelPowderSkyDark
val Pink40 = PastelStrawberryDark

// =========================================================================
// IMMUTABLE COZY PALETTE SYSTEM TOKEN CLASS
// =========================================================================
@Immutable
data class CozyThemeColors(
    val background: Color = CreamLinenBackground,
    val backgroundAlt: Color = CreamSandBackground,
    val surface: Color = CreamCloudSurface,
    val surfaceSubtle: Color = CreamSubtleSurface,
    val surfaceElevated: Color = CreamElevatedSurface,
    val boardMat: Color = CreamBoardMat,
    val dockTray: Color = CreamDockTray,
    val dockSlotBg: Color = CreamDockSlotBg,
    val textPrimary: Color = CozyEspressoText,
    val textSecondary: Color = CozyCappuccinoText,
    val textMuted: Color = CozyTaupeMutedText,
    val softBlue: Color = PastelSoftBlue,
    val softBlueDark: Color = PastelSoftBlueDark,
    val softBlueBg: Color = PastelSoftBlueBg,
    val palePink: Color = PalePinkPastel,
    val palePinkDark: Color = PalePinkDark,
    val palePinkBg: Color = PalePinkBg,
    val warmCream: Color = WarmVanillaCream,
    val warmCreamDark: Color = WarmVanillaDark,
    val warmCreamBg: Color = WarmVanillaBg,
    val softMint: Color = SoftMintPastel,
    val softMintDark: Color = SoftMintDark,
    val softMintBg: Color = SoftMintBg,
    val softLilac: Color = SoftLilacPastel,
    val softLilacDark: Color = SoftLilacDark,
    val softLilacBg: Color = SoftLilacBg,
    val softPeach: Color = SoftPeachPastel,
    val softPeachDark: Color = SoftPeachDark,
    val softPeachBg: Color = SoftPeachBg
)

val LocalCozyColors = staticCompositionLocalOf { CozyThemeColors() }
