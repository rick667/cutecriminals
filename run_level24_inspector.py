import subprocess

cmd = """
kotlinc -script - << 'KT'
import java.util.Random

val levelNumber = 24
val seed = levelNumber * 521L + 17L
val rng = Random(seed)

println("Java Random seed test:")
val sample = (0 until 5).map { rng.nextInt(100) }
println(sample)
KT
"""
