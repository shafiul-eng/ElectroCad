package com.example.repository

import com.example.data.local.CircuitDesignDao
import com.example.data.local.CircuitDesignEntity
import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentType
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class CircuitRepository(private val dao: CircuitDesignDao) {

    val allDesigns: Flow<List<CircuitDesignEntity>> = dao.getAllDesigns()

    suspend fun saveDesign(name: String, description: String, components: List<CircuitComponent>, wires: List<CircuitWire>): Long {
        val compArray = JSONArray()
        for (c in components) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("type", c.type.name)
                put("label", c.label)
                put("value", c.value.toDouble())
                put("x", c.x.toDouble())
                put("y", c.y.toDouble())
                put("rotationAngle", c.rotationAngle.toDouble())
                put("scale", c.scale.toDouble())
                put("isSwitchClosed", c.isSwitchClosed)
                put("ledColor", c.ledColor)
                put("customCompanyName", c.customCompanyName ?: "")
            }
            compArray.put(obj)
        }

        val wireArray = JSONArray()
        for (w in wires) {
            val obj = JSONObject().apply {
                put("id", w.id)
                put("fromComponentId", w.fromComponentId)
                put("fromPinId", w.fromPinId)
                put("toComponentId", w.toComponentId)
                put("toPinId", w.toPinId)
            }
            wireArray.put(obj)
        }

        val entity = CircuitDesignEntity(
            name = name,
            description = description,
            timestamp = System.currentTimeMillis(),
            componentsJson = compArray.toString(),
            wiresJson = wireArray.toString(),
            componentCount = components.size
        )
        return dao.insertDesign(entity)
    }

    suspend fun deleteDesign(id: Long) {
        dao.deleteDesignById(id)
    }

    companion object {
        fun deserializeDesign(entity: CircuitDesignEntity): Pair<List<CircuitComponent>, List<CircuitWire>> {
            val components = mutableListOf<CircuitComponent>()
            val wires = mutableListOf<CircuitWire>()

            try {
                val compArray = JSONArray(entity.componentsJson)
                for (i in 0 until compArray.length()) {
                    val obj = compArray.getJSONObject(i)
                    val typeStr = obj.optString("type", ComponentType.RESISTOR.name)
                    val type = try {
                        ComponentType.valueOf(typeStr)
                    } catch (e: Exception) {
                        ComponentType.RESISTOR
                    }

                    val comp = CircuitComponent(
                        id = obj.optString("id"),
                        type = type,
                        label = obj.optString("label", type.nameBn),
                        value = obj.optDouble("value", type.defaultVal.toDouble()).toFloat(),
                        x = obj.optDouble("x", 100.0).toFloat(),
                        y = obj.optDouble("y", 100.0).toFloat(),
                        rotationAngle = obj.optDouble("rotationAngle", 0.0).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        isSwitchClosed = obj.optBoolean("isSwitchClosed", true),
                        ledColor = obj.optString("ledColor", "RED"),
                        customCompanyName = obj.optString("customCompanyName").takeIf { it.isNotBlank() }
                    )
                    components.add(comp)
                }

                val wireArray = JSONArray(entity.wiresJson)
                for (i in 0 until wireArray.length()) {
                    val obj = wireArray.getJSONObject(i)
                    val wire = CircuitWire(
                        id = obj.optString("id"),
                        fromComponentId = obj.optString("fromComponentId"),
                        fromPinId = obj.optString("fromPinId"),
                        toComponentId = obj.optString("toComponentId"),
                        toPinId = obj.optString("toPinId")
                    )
                    wires.add(wire)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return Pair(components, wires)
        }
    }
}
