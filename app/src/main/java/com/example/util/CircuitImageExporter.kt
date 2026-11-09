package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.core.content.FileProvider
import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import java.io.File
import java.io.FileOutputStream

object CircuitImageExporter {

    fun exportAndShareCircuit(
        context: Context,
        circuitTitle: String,
        components: List<CircuitComponent>,
        wires: List<CircuitWire>
    ) {
        val width = 1200
        val height = 1600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background
        canvas.drawColor(Color.parseColor("#0A0F1D"))

        val gridPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        for (x in 0..width step 40) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }
        for (y in 0..height step 40) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }

        // 2. Draw Title & Watermark Header
        val titlePaint = Paint().apply {
            color = Color.parseColor("#00E5FF")
            textSize = 44f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("ElectroCAD — $circuitTitle", 40f, 70f, titlePaint)

        val creditPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 28f
            isAntiAlias = true
        }
        canvas.drawText("Developed by Md Shafiul Islam • High-Resolution Export", 40f, 110f, creditPaint)

        // 3. Scale factor for canvas coordinates
        val scale = 3.2f

        // Draw Wires
        val wirePaint = Paint().apply {
            color = Color.parseColor("#00F5D4")
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
        for (w in wires) {
            val from = components.find { it.id == w.fromComponentId }
            val to = components.find { it.id == w.toComponentId }
            if (from != null && to != null) {
                val fromPin = from.getPins().find { it.id == w.fromPinId }
                val toPin = to.getPins().find { it.id == w.toPinId }
                if (fromPin != null && toPin != null) {
                    val p1x = (from.x + fromPin.relX * 55f) * scale
                    val p1y = (from.y + fromPin.relY * 55f) * scale + 150f
                    val p2x = (to.x + toPin.relX * 55f) * scale
                    val p2y = (to.y + toPin.relY * 55f) * scale + 150f

                    val path = Path().apply {
                        moveTo(p1x, p1y)
                        val midX = (p1x + p2x) / 2f
                        lineTo(midX, p1y)
                        lineTo(midX, p2y)
                        lineTo(p2x, p2y)
                    }
                    canvas.drawPath(path, wirePaint)
                }
            }
        }

        // Draw Components
        val compBgPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val compStrokePaint = Paint().apply {
            color = Color.parseColor("#334155")
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        val compTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 26f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        val subTextPaint = Paint().apply {
            color = Color.parseColor("#00E5FF")
            textSize = 22f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        for (comp in components) {
            val cx = comp.x * scale
            val cy = comp.y * scale + 150f
            val radius = 38f * scale

            canvas.drawCircle(cx, cy, radius, compBgPaint)
            canvas.drawCircle(cx, cy, radius, compStrokePaint)

            canvas.drawText(comp.type.iconSymbol, cx, cy + 10f, compTextPaint)
            canvas.drawText(comp.label, cx, cy + radius + 30f, subTextPaint)
        }

        // 4. Save to cache and share via Intent
        try {
            val imagesFolder = File(context.cacheDir, "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "electrocad_circuit_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "ElectroCAD Circuit Design: $circuitTitle")
                putExtra(Intent.EXTRA_TEXT, "Exported from ElectroCAD • Developed by Md Shafiul Islam")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share ElectroCAD Circuit Design").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
