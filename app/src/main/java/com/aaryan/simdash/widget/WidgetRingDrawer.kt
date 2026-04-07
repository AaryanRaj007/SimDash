package com.aaryan.simdash.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

object WidgetRingDrawer {
    fun drawRingBitmap(
        percent: Float,
        ringColor: Int,
        isDark: Boolean,
        context: Context,
        widgetWidthPx: Int
    ): Bitmap {
        // Use actual widget width in pixels -- avoiding hardcoded values (Vulnerability 7 Fix)
        val density = context.resources.displayMetrics.density
        val size = (widgetWidthPx * 0.4f).toInt().coerceAtLeast((75 * density).toInt())
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val strokeWidth = size * 0.12f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        
        val rect = RectF(
            strokeWidth / 2f,
            strokeWidth / 2f,
            size - strokeWidth / 2f,
            size - strokeWidth / 2f
        )
        
        // Draw track
        paint.color = if (isDark) 0xFF2A2A4A.toInt() else 0xFFE8E6FF.toInt()
        paint.strokeWidth = strokeWidth
        canvas.drawArc(rect, 0f, 360f, false, paint)
        
        // Draw progress with subtle glow
        paint.color = ringColor
        paint.setShadowLayer(8f, 0f, 0f, ringColor)
        val sweepAngle = (percent * 360f).coerceIn(0f, 360f)
        canvas.drawArc(rect, 270f, sweepAngle, false, paint)
        
        return bitmap
    }
}