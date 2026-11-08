package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Logo3DAnimation(
  modifier: Modifier = Modifier,
  size: Dp = 160.dp,
  speedMultiplier: Float = 1.0f
) {
  // Infinite rotation transitions for 3D effect
  val infiniteTransition = rememberInfiniteTransition(label = "3DLogoTransition")

  // Turntable Y-axis rotation
  val rotY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween((3600 / speedMultiplier).toInt(), easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotY"
  )

  // Floating bobbing motion & X-axis tilt oscillation
  val bobbing by infiniteTransition.animateFloat(
    initialValue = -12f,
    targetValue = 12f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bobbing"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val canvasWidth = this.size.width
      val canvasHeight = this.size.height
      val centerX = canvasWidth / 2f
      val centerY = canvasHeight / 2f + bobbing
      val radius = (canvasWidth.coerceAtMost(canvasHeight) / 2f) * 0.75f * pulseScale

      // 1. Radiant Background Glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            SaffronPrimary.copy(alpha = 0.35f),
            RoyalIndigo.copy(alpha = 0.20f),
            Color.Transparent
          ),
          center = Offset(centerX, centerY),
          radius = radius * 1.4f
        ),
        radius = radius * 1.4f,
        center = Offset(centerX, centerY)
      )

      // 2. 3D Gyroscope Orbit Rings (Simulated 3D perspective projection)
      val radY = Math.toRadians(rotY.toDouble())
      val tiltX = Math.toRadians((25.0 + sin(radY) * 15.0))

      // Outer Orbiting Ring 1 (Saffron / Sunset)
      draw3DOrbitRing(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 1.15f,
        rotY = rotY,
        tiltX = tiltX,
        color = SaffronPrimary,
        strokeWidth = 3.5.dp.toPx()
      )

      // Outer Orbiting Ring 2 (Peacock Cyan)
      draw3DOrbitRing(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.95f,
        rotY = -rotY * 1.2f,
        tiltX = -tiltX * 0.8,
        color = PeacockCyan,
        strokeWidth = 2.8.dp.toPx()
      )

      // 3. Central 3D Extruded Diamond/Hexagon Emblem ("A" for Apna)
      draw3DExtrudedEmblem(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.65f,
        rotY = rotY,
        tiltX = tiltX
      )

      // 4. Orbiting Glowing Particles / Social Nodes
      val nodeAngles = listOf(0.0, 120.0, 240.0)
      for (angle in nodeAngles) {
        val totalRad = Math.toRadians((rotY.toDouble() + angle))
        // Project 3D point (x, y, z) on sphere
        val px = (centerX + (cos(totalRad) * radius.toDouble() * 1.15)).toFloat()
        val py = (centerY + (sin(totalRad) * sin(tiltX) * radius.toDouble() * 1.15)).toFloat()
        val pz = (sin(totalRad) * cos(tiltX)).toFloat() // depth factor (-1 to +1)

        val nodeRadius = (6f + pz * 3f).coerceAtLeast(3f).dp.toPx()
        val alpha = (0.5f + pz * 0.5f).coerceIn(0.2f, 1.0f)

        drawCircle(
          color = if (angle == 0.0) SaffronPrimary.copy(alpha = alpha) else PeacockCyan.copy(alpha = alpha),
          radius = nodeRadius,
          center = Offset(px, py)
        )
      }
    }
  }
}

private fun DrawScope.draw3DOrbitRing(
  centerX: Float,
  centerY: Float,
  radius: Float,
  rotY: Float,
  tiltX: Double,
  color: Color,
  strokeWidth: Float
) {
  val path = Path()
  val steps = 60
  var first = true

  for (i in 0..steps) {
    val angle = (i.toDouble() / steps) * 2 * PI
    val x = radius * cos(angle)
    val z = radius * sin(angle)

    // Rotate around Y
    val radY = Math.toRadians(rotY.toDouble())
    val rx = x * cos(radY) - z * sin(radY)
    val rz = x * sin(radY) + z * cos(radY)

    // Tilt X
    val ry = rz * sin(tiltX)

    val projX = (centerX + rx).toFloat()
    val projY = (centerY + ry).toFloat()

    if (first) {
      path.moveTo(projX, projY)
      first = false
    } else {
      path.lineTo(projX, projY)
    }
  }

  drawPath(
    path = path,
    color = color.copy(alpha = 0.85f),
    style = Stroke(width = strokeWidth)
  )
}

private fun DrawScope.draw3DExtrudedEmblem(
  centerX: Float,
  centerY: Float,
  radius: Float,
  rotY: Float,
  tiltX: Double
) {
  // 3D Isometric Extrusion: Layered slices along Z to create true volumetric depth
  val layers = 8
  val depthSpacing = 2.5f

  for (l in 0..layers) {
    val depthOffset = (l - layers / 2f) * depthSpacing
    val radY = Math.toRadians(rotY.toDouble())
    val layerX = centerX + (sin(radY) * depthOffset).toFloat()
    val layerY = centerY + (cos(radY) * sin(tiltX) * depthOffset).toFloat()

    val layerAlpha = if (l == layers) 1.0f else (0.4f + (l.toFloat() / layers) * 0.4f)
    val layerColor = if (l == layers) {
      SaffronPrimary
    } else if (l > layers / 2) {
      RoyalIndigo
    } else {
      Color(0xFF2A1B4E)
    }

    // 6-sided 3D gem shape
    val path = Path()
    val sides = 6
    for (s in 0 until sides) {
      val angle = Math.toRadians(rotY.toDouble() + (s * 360.0 / sides))
      val sx = layerX + (cos(angle) * radius).toFloat()
      val sy = layerY + (sin(angle) * sin(tiltX) * radius).toFloat()

      if (s == 0) path.moveTo(sx, sy) else path.lineTo(sx, sy)
    }
    path.close()

    drawPath(
      path = path,
      brush = Brush.linearGradient(
        colors = listOf(
          layerColor.copy(alpha = layerAlpha),
          RoyalIndigo.copy(alpha = layerAlpha * 0.8f)
        ),
        start = Offset(layerX - radius, layerY - radius),
        end = Offset(layerX + radius, layerY + radius)
      )
    )

    if (l == layers) {
      // Golden border & center core node on front face
      drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.9f),
        style = Stroke(width = 2.dp.toPx())
      )

      drawCircle(
        color = Color.White,
        radius = radius * 0.22f,
        center = Offset(layerX, layerY)
      )
      drawCircle(
        color = SaffronPrimary,
        radius = radius * 0.14f,
        center = Offset(layerX, layerY)
      )
    }
  }
}
