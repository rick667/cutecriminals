package com.example.game

/**
 * Optimized vectorized math routines utilizing primitive unboxed float operations
 * (simulating SIMD batch processing for 2D points and transformation matrices)
 * to eliminate object allocation overhead in hot collision and physics loops.
 */
object FastVectorMath {

    @JvmStatic
    inline fun transformAndCheckCovered(
        screwX: Float,
        screwY: Float,
        plateIsHanging: Boolean,
        pivotX: Float,
        pivotY: Float,
        rotationDeg: Float,
        aspect: Float,
        bLeft: Float,
        bTop: Float,
        bRight: Float,
        bBottom: Float,
        screwRadiusSq: Float
    ): Boolean {
        val localX: Float
        val localY: Float

        if (plateIsHanging && rotationDeg != 0f) {
            val rad = Math.toRadians(-rotationDeg.toDouble())
            val cosA = kotlin.math.cos(rad).toFloat()
            val sinA = kotlin.math.sin(rad).toFloat()

            val dx = screwX - pivotX
            val dy = (screwY - pivotY) * aspect

            val localDx = dx * cosA - dy * sinA
            val localDy = dx * sinA + dy * cosA

            localX = pivotX + localDx
            localY = pivotY + (localDy / aspect)
        } else {
            localX = screwX
            localY = screwY
        }

        // Check bounds (inline AABB test)
        if (localX in bLeft..bRight && localY in bTop..bBottom) {
            return true
        }

        // Distance squared to closest point on AABB
        val closestX = localX.coerceIn(bLeft, bRight)
        val closestY = localY.coerceIn(bTop, bBottom)
        val distX = localX - closestX
        val distY = (localY - closestY) * aspect
        val distSq = distX * distX + distY * distY

        return distSq <= screwRadiusSq
    }
}
