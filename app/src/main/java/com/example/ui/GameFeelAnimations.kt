package com.example.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.game.ScrewColor
import com.example.game.ScrewSkinType
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Shape types for expressive, juicy, and cozy particle effects.
 */
enum class ParticleShapeType {
    SOFT_SPARKLE,      // 4-pointed diamond star with radiant white core
    RADIANT_STAR,      // Multi-pointed dazzling star with light rays
    FACETED_GEM,       // 3D faceted gemstone shard with light & shadow facets
    CONFETTI_RIBBON,   // 3D tumbling paper/foil confetti ribbon with perspective flip
    DUST_CLOUD,        // Soft organic billowing cloud puff with volumetric highlights
    PASTEL_HALO,       // Gentle expanding shockwave ring
    MAGIC_ORB          // Glowing energy spark with outer aura
}

/**
 * Expressive particle item definition with rotation, growth, gravity, drag, and 3D tumbling.
 */
data class ParticleItem(
    val shape: ParticleShapeType = ParticleShapeType.SOFT_SPARKLE,
    val velocity: Offset = Offset.Zero,
    val initialRadius: Float = 4.0f,
    val color: Color = Color.White,
    val secondaryColor: Color = Color.White,
    val angle: Float = 0f,
    val rotationSpeed: Float = 0f,
    val growthFactor: Float = 0f,     // > 0 expands radius over time
    val gravity: Float = 0f,          // > 0 falls downward, < 0 floats upward
    val drag: Float = 0.88f,          // Air resistance damping (0f..1f)
    val alphaMultiplier: Float = 1f,
    val twinkleFreq: Float = 0f,      // > 0 adds sparkle twinkle
    val flipSpeed: Float = 0f,        // 3D tumble flip speed for ribbons
    val spawnTimeOffsetMs: Long = 0L
)

/**
 * Container for active particle bursts on screen.
 */
data class ParticleBurstEffect(
    val id: String,
    val center: Offset,
    val color: Color,
    val particles: List<ParticleItem>,
    val startTimeMs: Long,
    val durationMs: Long = 650L
)

/**
 * Visual confirmation tap ripple on screen at click location.
 */
data class TapRippleItem(
    val id: Long,
    val position: Offset,
    val startTimeMs: Long,
    val color: Color = Color(0xFFFFFFFF),
    val durationMs: Long = 340L,
    val maxRadius: Float = 36f
)

/**
 * Floating score popup that bounces upward upon match and floats to the top score counter.
 */
data class FloatingScoreItem(
    val id: Long,
    val text: String,
    val startPosition: Offset,
    val targetPosition: Offset = Offset.Zero,
    val color: Color,
    val startTimeMs: Long,
    val durationMs: Long = 1150L,
    val scoreValue: Int = 300,
    val multiplier: Float = 1.0f
)

/**
 * Active trajectory state for flying screws.
 */
data class ActiveFlyingScrew(
    val id: String,
    val screwColor: ScrewColor,
    val skin: ScrewSkinType,
    val startOffset: Offset,
    val targetOffset: Offset,
    val controlOffset: Offset,
    val startTimeNanos: Long,
    val durationMillis: Long = 450L,
    val pattern: com.example.game.ScrewPattern = com.example.game.ScrewPattern.NONE
)

/**
 * Active trajectory state for magical progress energy particles flying to the header progress bar.
 */
data class FlyingProgressParticle(
    val id: String,
    val color: Color,
    val startOffset: Offset,
    val targetOffset: Offset,
    val controlOffset: Offset,
    val startTimeNanos: Long,
    val durationMillis: Long = 420L,
    val radius: Float = 6f
)

/**
 * Object Pool Manager for Particle Bursts to guarantee Zero-GC during rapid puzzle interactions.
 */
object ParticleObjectPool {
    private const val MAX_BURST_POOL_SIZE = 32
    private val burstPool = ArrayDeque<MutableList<ParticleItem>>(MAX_BURST_POOL_SIZE)

    fun obtainParticleList(capacity: Int = 16): MutableList<ParticleItem> {
        return if (burstPool.isNotEmpty()) {
            val list = burstPool.removeFirst()
            list.clear()
            list
        } else {
            ArrayList(capacity)
        }
    }

    fun releaseParticleList(list: List<ParticleItem>) {
        if (burstPool.size < MAX_BURST_POOL_SIZE && list is MutableList<ParticleItem>) {
            list.clear()
            burstPool.addLast(list)
        }
    }
}

/**
 * Factory and Prefabs for Low-Poly Pastel Particle Effects.
 */
object ParticleEffectFactory {

    /**
     * Cozy Unscrew Prefab: Combination of radiant sparkling stars, faceted gem shards, and warm linen dust clouds.
     */
    fun createUnscrewCozyBurst(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(20)

        // 1. Radiant Sparkles (Twinkling star bursts shooting outward with air drag)
        val sparkleCount = 8
        for (i in 0 until sparkleCount) {
            val angle = (i.toFloat() / sparkleCount) * 2f * Math.PI.toFloat() + (i * 0.18f)
            val speed = 12f + (i % 3) * 6f
            particles.add(
                ParticleItem(
                    shape = if (i % 2 == 0) ParticleShapeType.RADIANT_STAR else ParticleShapeType.SOFT_SPARKLE,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 6f),
                    initialRadius = 6.5f + (i % 2) * 2.5f,
                    color = color,
                    secondaryColor = Color.White,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 4.5f,
                    growthFactor = -0.2f,
                    gravity = 2.5f,
                    drag = 0.86f,
                    alphaMultiplier = 0.98f,
                    twinkleFreq = 16f + (i * 3f)
                )
            )
        }

        // 2. Faceted Gem Shards (Punchy colorful jewel chips)
        val gemCount = 4
        for (i in 0 until gemCount) {
            val angle = (i.toFloat() / gemCount) * 2f * Math.PI.toFloat() + 0.35f
            val speed = 9f + (i % 2) * 5f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.FACETED_GEM,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 4f),
                    initialRadius = 5.5f + (i % 2) * 2f,
                    color = color,
                    secondaryColor = WarmVanillaCream,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 5.0f,
                    growthFactor = -0.15f,
                    gravity = 4.0f,
                    drag = 0.88f,
                    alphaMultiplier = 0.95f
                )
            )
        }

        // 3. Small Cozy Dust Clouds (Warm linen / oatmeal dust puffs)
        val dustCount = 4
        for (i in 0 until dustCount) {
            val angle = (i.toFloat() / dustCount) * 2f * Math.PI.toFloat() + 0.5f
            val speed = 5f + (i % 2) * 3f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.DUST_CLOUD,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 3f),
                    initialRadius = 8f + (i % 2) * 3f,
                    color = CreamSandBackground,
                    angle = angle,
                    rotationSpeed = 0.6f,
                    growthFactor = 1.6f,
                    gravity = -1.2f, // buoyant lift
                    drag = 0.80f,
                    alphaMultiplier = 0.65f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 650L
        )
    }

    /**
     * Dock Fitting Snap Prefab: Expanding glowing shockwave, faceted gem shards, and radiant stars.
     */
    fun createDockImpactBurst(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(16)

        // 1. Expanding soft halo shockwave
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 10f,
                color = color,
                growthFactor = 4.0f,
                alphaMultiplier = 0.85f
            )
        )

        // 2. Radiant Star and Gem Shards
        val count = 10
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * 2f * Math.PI.toFloat()
            val speed = 14f + (i % 3) * 6f
            val isSparkle = (i % 2 == 0)
            particles.add(
                ParticleItem(
                    shape = if (isSparkle) ParticleShapeType.RADIANT_STAR else ParticleShapeType.FACETED_GEM,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 5f),
                    initialRadius = 6f + (i % 3) * 1.5f,
                    color = if (i % 3 == 0) Color.White else color,
                    secondaryColor = WarmVanillaCream,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 4.5f,
                    growthFactor = -0.25f,
                    gravity = 3.5f,
                    drag = 0.86f,
                    alphaMultiplier = 0.95f,
                    twinkleFreq = 14f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 550L
        )
    }

    /**
     * Progress Bar Impact Splash: Shimmering pastel sparkles and an expanding halo when an energy orb enters the progress bar.
     */
    fun createProgressBarImpactBurst(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(14)

        // 1. Expanding soft halo
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 8f,
                color = color,
                growthFactor = 3.2f,
                alphaMultiplier = 0.90f
            )
        )

        // 2. Twinkling radiant sparkles
        val count = 8
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * 2f * Math.PI.toFloat()
            val speed = 9f + (i % 2) * 5f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.RADIANT_STAR,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed * 0.7f),
                    initialRadius = 5f + (i % 2) * 2f,
                    color = if (i % 2 == 0) Color.White else color,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 4f,
                    growthFactor = -0.2f,
                    drag = 0.85f,
                    alphaMultiplier = 0.95f,
                    twinkleFreq = 18f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 500L
        )
    }

    /**
     * Plate Fall Dust Cushion Prefab: Soft pastel dust clouds when a heavy plate detaches and falls.
     */
    fun createPlateFallDustPuff(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(14)

        val dustCount = 8
        for (i in 0 until dustCount) {
            val angle = (i.toFloat() / dustCount) * Math.PI.toFloat() // lower hemisphere
            val speed = 7f + (i % 3) * 5f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.DUST_CLOUD,
                    velocity = Offset(cos(angle) * speed * 1.8f, sin(angle) * speed * 0.9f + 2f),
                    initialRadius = 11f + (i % 3) * 4f,
                    color = CreamSandBackground,
                    angle = angle,
                    rotationSpeed = 0.5f,
                    growthFactor = 1.7f,
                    gravity = -1.0f,
                    drag = 0.82f,
                    alphaMultiplier = 0.65f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 700L
        )
    }

    /**
     * Rewarding Plate Clearance Explosion Prefab:
     * Triggered when a player removes all screws locking a plate in place.
     * Features expanding pastel shockwave halos, 3D tumbling confetti ribbons,
     * high-velocity radiant gem shards, glittering star bursts and soft billowing dust cloud puffs.
     */
    fun createPlateClearExplosion(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(36)

        // 1. Double expanding pastel shockwave rings
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 14f,
                color = color,
                growthFactor = 5.8f,
                alphaMultiplier = 0.95f
            )
        )
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 8f,
                color = Color.White,
                growthFactor = 4.0f,
                alphaMultiplier = 0.85f
            )
        )

        // 2. 3D Tumbling Confetti Ribbons
        val confettiCount = 10
        for (i in 0 until confettiCount) {
            val angle = (i.toFloat() / confettiCount) * 2f * Math.PI.toFloat() + 0.15f
            val speed = 16f + (i % 3) * 7f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.CONFETTI_RIBBON,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 8f),
                    initialRadius = 7f + (i % 2) * 2.5f,
                    color = when (i % 3) {
                        0 -> color
                        1 -> WarmVanillaCream
                        else -> PalePinkPastel
                    },
                    secondaryColor = Color.White,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 5.0f,
                    growthFactor = -0.1f,
                    gravity = 5.0f,
                    drag = 0.88f,
                    alphaMultiplier = 0.95f,
                    flipSpeed = 6.0f + (i % 3) * 2f
                )
            )
        }

        // 3. Radiant 360-degree Faceted Gem Shards & Sparkling Stars
        val shardCount = 12
        for (i in 0 until shardCount) {
            val angle = (i.toFloat() / shardCount) * 2f * Math.PI.toFloat() + (i * 0.15f)
            val speed = 20f + (i % 3) * 9f
            val isStar = (i % 2 == 0)
            particles.add(
                ParticleItem(
                    shape = if (isStar) ParticleShapeType.RADIANT_STAR else ParticleShapeType.FACETED_GEM,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 7f),
                    initialRadius = 7.5f + (i % 3) * 2f,
                    color = when (i % 4) {
                        0 -> Color.White
                        1 -> color
                        2 -> WarmVanillaCream
                        else -> PalePinkPastel
                    },
                    secondaryColor = Color.White,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 6.5f,
                    growthFactor = -0.2f,
                    gravity = 4.0f,
                    drag = 0.86f,
                    alphaMultiplier = 1f,
                    twinkleFreq = 18f
                )
            )
        }

        // 4. Billowing Soft Dust Clouds on Plate Detachment
        val dustCount = 6
        for (i in 0 until dustCount) {
            val angle = (i.toFloat() / dustCount) * 2f * Math.PI.toFloat()
            val speed = 8f + (i % 2) * 4f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.DUST_CLOUD,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed + 2f),
                    initialRadius = 12f + (i % 3) * 4f,
                    color = CreamSandBackground,
                    angle = angle,
                    rotationSpeed = 0.6f,
                    growthFactor = 1.8f,
                    gravity = -0.8f,
                    drag = 0.82f,
                    alphaMultiplier = 0.65f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 800L
        )
    }

    /**
     * Match-3 Trio Celebration: Multi-layered pastel confetti, soft cloud puffs and twinkling stars.
     */
    fun createMatch3PastelCelebration(id: String, center: Offset, color: Color): ParticleBurstEffect {
        return createMatch3MergeExplosion(id, center, color)
    }

    /**
     * Ultra-Juicy Match-3 Merge Detonation Explosion:
     * When 3 matching screws converge to the centroid in the dock, they detonate
     * into a high-energy, dazzling explosion with triple expanding shockwaves,
     * 3D tumbling confetti ribbons, radiant 360-degree gem shards, sparkling stars, and billowing warm dust clouds.
     */
    fun createMatch3MergeExplosion(id: String, center: Offset, color: Color): ParticleBurstEffect {
        val particles = ParticleObjectPool.obtainParticleList(42)

        // 1. Triple expanding pastel & white shockwave halos
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 16f,
                color = color,
                growthFactor = 6.5f,
                alphaMultiplier = 0.95f
            )
        )
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 10f,
                color = Color.White,
                growthFactor = 4.8f,
                alphaMultiplier = 0.90f
            )
        )
        particles.add(
            ParticleItem(
                shape = ParticleShapeType.PASTEL_HALO,
                velocity = Offset.Zero,
                initialRadius = 6f,
                color = WarmVanillaCream,
                growthFactor = 3.5f,
                alphaMultiplier = 0.85f
            )
        )

        // 2. 3D Tumbling Confetti Ribbons (Paper & Foil streamers twisting in mid-air)
        val confettiCount = 14
        for (i in 0 until confettiCount) {
            val angle = (i.toFloat() / confettiCount) * 2f * Math.PI.toFloat() + 0.2f
            val speed = 18f + (i % 3) * 8f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.CONFETTI_RIBBON,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 10f),
                    initialRadius = 8f + (i % 2) * 3f,
                    color = when (i % 4) {
                        0 -> color
                        1 -> WarmVanillaCream
                        2 -> PalePinkPastel
                        else -> SoftMintPastel
                    },
                    secondaryColor = Color.White,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 5.5f,
                    growthFactor = -0.1f,
                    gravity = 5.2f,
                    drag = 0.88f,
                    alphaMultiplier = 0.95f,
                    flipSpeed = 6.5f + (i % 3) * 2f
                )
            )
        }

        // 3. Radiant 360-degree Faceted Gem Shards & Sparkling Stars
        val shardCount = 16
        for (i in 0 until shardCount) {
            val angle = (i.toFloat() / shardCount) * 2f * Math.PI.toFloat() + (i * 0.18f)
            val speed = 22f + (i % 3) * 10f
            val isStar = (i % 2 == 0)
            particles.add(
                ParticleItem(
                    shape = if (isStar) ParticleShapeType.RADIANT_STAR else ParticleShapeType.FACETED_GEM,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed - 8f),
                    initialRadius = 8f + (i % 3) * 2.5f,
                    color = when (i % 4) {
                        0 -> Color.White
                        1 -> color
                        2 -> WarmVanillaCream
                        else -> PalePinkPastel
                    },
                    secondaryColor = Color.White,
                    angle = angle,
                    rotationSpeed = (i % 2 * 2 - 1) * 7.0f,
                    growthFactor = -0.2f,
                    gravity = 3.8f,
                    drag = 0.86f,
                    alphaMultiplier = 1f,
                    twinkleFreq = 20f
                )
            )
        }

        // 4. Billowing Soft Dust Clouds on Merge Detonation
        val dustCount = 6
        for (i in 0 until dustCount) {
            val angle = (i.toFloat() / dustCount) * 2f * Math.PI.toFloat()
            val speed = 8f + (i % 2) * 4f
            particles.add(
                ParticleItem(
                    shape = ParticleShapeType.DUST_CLOUD,
                    velocity = Offset(cos(angle) * speed, sin(angle) * speed + 2f),
                    initialRadius = 12f + (i % 3) * 4f,
                    color = CreamSandBackground,
                    angle = angle,
                    rotationSpeed = 0.6f,
                    growthFactor = 1.8f,
                    gravity = -0.8f,
                    drag = 0.82f,
                    alphaMultiplier = 0.65f
                )
            )
        }

        return ParticleBurstEffect(
            id = id,
            center = center,
            color = color,
            particles = particles,
            startTimeMs = System.currentTimeMillis(),
            durationMs = 850L
        )
    }

    /**
     * Quadratic Bezier Curve position calculation for trajectory arcs.
     */
    fun calculateQuadraticBezier(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
        val u = 1f - t
        val tt = t * t
        val uu = u * u
        val x = uu * p0.x + 2f * u * t * p1.x + tt * p2.x
        val y = uu * p0.y + 2f * u * t * p1.y + tt * p2.y
        return Offset(x, y)
    }

    // =========================================================================
    // CANVAS DRAW HELPERS FOR HARDWARE ACCELERATED JUICY PARTICLES
    // =========================================================================

    // Pre-allocated static unit paths (normalized from -1f to 1f) for 100% Zero-GC particle rendering
    private val unitSparklePath = Path().apply {
        val half = 1f
        val pinch = 0.26f
        moveTo(0f, -half)
        lineTo(pinch, -pinch)
        lineTo(half, 0f)
        lineTo(pinch, pinch)
        lineTo(0f, half)
        lineTo(-pinch, pinch)
        lineTo(-half, 0f)
        lineTo(-pinch, -pinch)
        close()
    }

    private val unitRadiantStarPath = Path().apply {
        val h = 1.15f
        val w = 1.15f
        val inner = 0.20f
        val diag = 0.58f
        val diagInner = 0.25f

        moveTo(0f, -h)
        lineTo(diagInner, -diagInner)
        lineTo(diag, -diag)
        lineTo(inner, 0f)
        lineTo(w, 0f)
        lineTo(inner, 0f)
        lineTo(diag, diag)
        lineTo(diagInner, diagInner)
        lineTo(0f, h)
        lineTo(-diagInner, diagInner)
        lineTo(-diag, diag)
        lineTo(-inner, 0f)
        lineTo(-w, 0f)
        lineTo(-inner, 0f)
        lineTo(-diag, -diag)
        lineTo(-diagInner, -diagInner)
        close()
    }

    private val unitGemFacetLeft = Path().apply {
        moveTo(0f, -1.2f)
        lineTo(-0.85f, 0f)
        lineTo(0f, 1.2f)
        close()
    }

    private val unitGemFacetRight = Path().apply {
        moveTo(0f, -1.2f)
        lineTo(0.85f, 0f)
        lineTo(0f, 1.2f)
        close()
    }

    /**
     * Draws a dazzling multi-pointed star with radiant beams, white core, and twinkling shimmer.
     */
    fun drawRadiantStar(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        rotationRad: Float,
        color: Color,
        alpha: Float,
        twinkleT: Float = 0f
    ) {
        val twinkleScale = 1f + (sin(twinkleT) * 0.22f)
        val finalSize = size * twinkleScale
        val clampedAlpha = (alpha * 0.98f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || finalSize <= 0.5f) return

        // 1. Soft glowing bloom aura behind star
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha * 0.40f),
            radius = finalSize * 1.6f,
            center = center
        )

        // 2. Star rays
        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(finalSize, finalSize, Offset.Zero)
        }) {
            drawPath(path = unitRadiantStarPath, color = color.copy(alpha = clampedAlpha))
        }

        // 3. Crisp white center diamond core
        val coreSize = finalSize * 0.48f
        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(coreSize, coreSize, Offset.Zero)
        }) {
            drawPath(path = unitSparklePath, color = Color.White.copy(alpha = clampedAlpha))
        }
    }

    /**
     * Draws a 4-point diamond sparkle star with gentle rotation and bright white core.
     */
    fun drawSoftSparkle(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        rotationRad: Float,
        color: Color,
        alpha: Float,
        twinkleT: Float = 0f
    ) {
        val twinkleScale = 1f + (sin(twinkleT) * 0.18f)
        val finalSize = size * twinkleScale
        val clampedAlpha = (alpha * 0.95f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || finalSize <= 0.5f) return

        // Outer glow
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha * 0.30f),
            radius = finalSize * 1.3f,
            center = center
        )

        // Colored body
        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(finalSize, finalSize, Offset.Zero)
        }) {
            drawPath(path = unitSparklePath, color = color.copy(alpha = clampedAlpha))
        }

        // Inner white shine
        val coreSize = finalSize * 0.45f
        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(coreSize, coreSize, Offset.Zero)
        }) {
            drawPath(path = unitSparklePath, color = Color.White.copy(alpha = clampedAlpha))
        }
    }

    /**
     * Draws a 3D faceted gemstone shard with distinct light and shadow facets plus a specular shine edge.
     */
    fun drawFacetedGem(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        rotationRad: Float,
        color: Color,
        secondaryColor: Color,
        alpha: Float
    ) {
        val clampedAlpha = (alpha * 0.95f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || size <= 0.5f) return

        val lightFacetColor = secondaryColor.copy(alpha = clampedAlpha)
        val mainFacetColor = color.copy(alpha = clampedAlpha)

        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(size, size, Offset.Zero)
        }) {
            // Shadow / main facet
            drawPath(path = unitGemFacetLeft, color = mainFacetColor)
            // Light highlight facet
            drawPath(path = unitGemFacetRight, color = lightFacetColor)
            // Specular ridge line down the center
            drawLine(
                color = Color.White.copy(alpha = clampedAlpha * 0.85f),
                start = Offset(0f, -1.2f),
                end = Offset(0f, 1.2f),
                strokeWidth = 0.12f
            )
        }
    }

    /**
     * Draws a 3D tumbling confetti ribbon that twists in space with perspective scale flip.
     */
    fun drawConfettiRibbon(
        drawScope: DrawScope,
        center: Offset,
        size: Float,
        rotationRad: Float,
        flipAngleRad: Float,
        color: Color,
        secondaryColor: Color,
        alpha: Float
    ) {
        val clampedAlpha = (alpha * 0.95f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || size <= 0.5f) return

        // 3D perspective flip factor: between -1f and 1f
        val flipScale = cos(flipAngleRad)
        val absFlip = kotlin.math.abs(flipScale).coerceAtLeast(0.12f)
        // Alternating face color: front is primary, back is shiny highlight
        val faceColor = if (flipScale >= 0f) color else secondaryColor

        drawScope.withTransform({
            translate(center.x, center.y)
            rotate(rotationRad * 57.29578f)
            scale(size * absFlip, size * 1.5f, Offset.Zero)
        }) {
            // Rectangular tumbling ribbon
            drawRect(
                color = faceColor.copy(alpha = clampedAlpha),
                topLeft = Offset(-0.8f, -0.6f),
                size = Size(1.6f, 1.2f)
            )
            // Delicate gloss line
            drawLine(
                color = Color.White.copy(alpha = clampedAlpha * 0.5f),
                start = Offset(-0.8f, -0.6f),
                end = Offset(0.8f, -0.6f),
                strokeWidth = 0.2f
            )
        }
    }

    /**
     * Draws a soft organic billowing dust cloud puff with volumetric lobes.
     */
    fun drawDustCloud(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        rotationRad: Float,
        color: Color,
        alpha: Float
    ) {
        val cloudAlpha = (alpha * 0.60f).coerceIn(0f, 1f)
        if (cloudAlpha <= 0.01f || radius <= 0.5f) return
        val mainColor = color.copy(alpha = cloudAlpha)
        val highlightColor = Color.White.copy(alpha = cloudAlpha * 0.55f)

        // Central soft puff
        drawScope.drawCircle(color = mainColor, radius = radius, center = center)

        // Auxiliary organic lobe puffs
        val r055 = radius * 0.55f
        val r045 = radius * 0.45f
        val r050 = radius * 0.50f
        val lobe1 = center + Offset(cos(rotationRad) * r055, sin(rotationRad) * r045)
        val lobe2 = center + Offset(cos(rotationRad + 2.1f) * r050, sin(rotationRad + 2.1f) * r050)
        val lobe3 = center + Offset(cos(rotationRad + 4.2f) * r045, sin(rotationRad + 4.2f) * r045)

        drawScope.drawCircle(color = mainColor, radius = radius * 0.72f, center = lobe1)
        drawScope.drawCircle(color = mainColor, radius = radius * 0.65f, center = lobe2)
        drawScope.drawCircle(color = mainColor, radius = radius * 0.60f, center = lobe3)

        // Soft cloud highlight glaze
        drawScope.drawCircle(
            color = highlightColor,
            radius = radius * 0.42f,
            center = center - Offset(radius * 0.22f, radius * 0.22f)
        )
    }

    /**
     * Draws an expanding soft pastel shockwave halo ring with luminous gradient edge.
     */
    fun drawPastelHalo(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        strokeWidth: Float,
        color: Color,
        alpha: Float
    ) {
        val clampedAlpha = (alpha * 0.85f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || radius <= 0.5f) return

        // Outer soft glow aura
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha * 0.25f),
            radius = radius + strokeWidth,
            center = center,
            style = Stroke(width = strokeWidth * 2.2f)
        )

        // Sharp ring
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )
    }

    /**
     * Draws a glowing energy spark with radiant radial glow and white hot core.
     */
    fun drawMagicOrb(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        color: Color,
        alpha: Float
    ) {
        val clampedAlpha = (alpha * 0.95f).coerceIn(0f, 1f)
        if (clampedAlpha <= 0.01f || radius <= 0.5f) return

        // Radiant bloom aura
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha * 0.45f),
            radius = radius * 2.2f,
            center = center
        )

        // Main colored core
        drawScope.drawCircle(
            color = color.copy(alpha = clampedAlpha),
            radius = radius,
            center = center
        )

        // White hot center
        drawScope.drawCircle(
            color = Color.White.copy(alpha = clampedAlpha * 0.95f),
            radius = radius * 0.45f,
            center = center
        )
    }

    /**
     * Draws a subtle, circular ripple effect with soft transparency at the tap location.
     */
    fun drawTapRipple(
        drawScope: DrawScope,
        ripple: TapRippleItem,
        nowMs: Long
    ) {
        val elapsed = (nowMs - ripple.startTimeMs).toFloat()
        val progress = (elapsed / ripple.durationMs).coerceIn(0f, 1f)
        if (progress >= 1f) return

        // Smooth cubic ease-out
        val easedT = 1f - (1f - progress).pow(3)
        val currentRadius = 6f + (ripple.maxRadius - 6f) * easedT
        val currentAlpha = (1f - progress) * 0.40f

        // 1. Subtle expanding outer ripple ring
        drawScope.drawCircle(
            color = ripple.color.copy(alpha = currentAlpha),
            radius = currentRadius,
            center = ripple.position,
            style = Stroke(width = (2.2f * (1f - progress * 0.6f)).coerceAtLeast(0.5f))
        )

        // 2. Soft translucent inner center glow
        if (progress < 0.5f) {
            val innerAlpha = (1f - (progress / 0.5f)) * 0.22f
            drawScope.drawCircle(
                color = ripple.color.copy(alpha = innerAlpha),
                radius = currentRadius * 0.45f,
                center = ripple.position
            )
        }
    }

    private fun rotatePoint(x: Float, y: Float, cosA: Float, sinA: Float): Offset {
        return Offset(x * cosA - y * sinA, x * sinA + y * cosA)
    }
}
