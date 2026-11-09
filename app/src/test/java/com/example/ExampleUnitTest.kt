package com.example

import com.example.data.DefaultCircuits
import com.example.engine.CalculatorEngine
import com.example.engine.CircuitEngine
import com.example.engine.CircuitHistoryManager
import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentType
import com.example.model.SimulationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCalculatorEngine_4BandResistor() {
        val (ohms, tol) = CalculatorEngine.calculate4Band(1, 0, 2, 10)
        assertEquals("1.00 kΩ", ohms)
        assertEquals("±5.0%", tol)
    }

    @Test
    fun testCalculatorEngine_LedResistor() {
        val (exact, nearestE12, powerW) = CalculatorEngine.calculateLedResistor(9f, 2f, 20f)
        assertEquals(350f, exact, 0.5f)
        assertEquals(330f, nearestE12, 1f)
        assertTrue(powerW > 0f)
    }

    @Test
    fun testCircuitEngine_PresetLedSimulation() {
        val preset = DefaultCircuits.getPresets().first()
        val result = CircuitEngine.solve(preset.components, preset.wires, isPaused = false)
        assertEquals(SimulationState.RUNNING_SAFE, result.state)
        assertTrue(result.totalCurrentMa > 0f)
        assertTrue(result.totalPowerMw > 0f)
    }

    @Test
    fun testCircuitEngine_ShortCircuitDetection() {
        val b = CircuitComponent(id = "bat", type = ComponentType.DC_BATTERY, label = "9V Battery", value = 9f, x = 50f, y = 50f)
        val directWire = CircuitWire(fromComponentId = "bat", fromPinId = "pos", toComponentId = "bat", toPinId = "neg")
        val result = CircuitEngine.solve(listOf(b), listOf(directWire), isPaused = false)
        assertEquals(SimulationState.SHORT_CIRCUIT, result.state)
    }

    @Test
    fun testCircuitHistoryManager_UndoRedo() {
        val manager = CircuitHistoryManager()
        assertFalse(manager.canUndo())
        assertFalse(manager.canRedo())

        val comp1 = CircuitComponent(id = "c1", type = ComponentType.RESISTOR, label = "R1", value = 100f, x = 10f, y = 10f)
        manager.pushState(listOf(comp1), emptyList())
        assertTrue(manager.canUndo())

        val comp2 = CircuitComponent(id = "c2", type = ComponentType.LED, label = "LED", value = 2f, x = 50f, y = 50f)
        val restored = manager.undo(listOf(comp1, comp2), emptyList())
        assertNotNull(restored)
        assertEquals(1, restored!!.components.size)
        assertTrue(manager.canRedo())

        val redone = manager.redo(restored.components, restored.wires)
        assertNotNull(redone)
        assertEquals(2, redone!!.components.size)
    }

    @Test
    fun testRotatedPinsGeometry() {
        val comp = CircuitComponent(
            id = "c1",
            type = ComponentType.RESISTOR,
            label = "R1",
            value = 100f,
            x = 100f,
            y = 100f,
            rotationAngle = 90f
        )
        val pins = comp.getPins()
        assertEquals(2, pins.size)
        // With 90 deg rotation, (relX, relY) = (-0.75, 0) rotated by 90deg becomes (0, -0.75)
        assertEquals(0f, pins[0].relX, 0.05f)
        assertEquals(-0.75f, pins[0].relY, 0.05f)
    }

    @Test
    fun testDiodeReversePolarityBlocksCurrent() {
        val battery = CircuitComponent(id = "bat", type = ComponentType.DC_BATTERY, label = "9V Battery", value = 9f, x = 50f, y = 50f)
        val led = CircuitComponent(id = "led", type = ComponentType.LED, label = "LED", value = 2f, x = 150f, y = 50f)
        val resistor = CircuitComponent(id = "r1", type = ComponentType.RESISTOR, label = "330Ω", value = 330f, x = 250f, y = 50f)

        // Connect LED in REVERSE BIAS: Battery (+) -> LED Cathode (-), LED Anode (+) -> Resistor -> Battery (-)
        val wires = listOf(
            CircuitWire(fromComponentId = "bat", fromPinId = "pos", toComponentId = "led", toPinId = "cathode"),
            CircuitWire(fromComponentId = "led", fromPinId = "anode", toComponentId = "r1", toPinId = "p1"),
            CircuitWire(fromComponentId = "r1", fromPinId = "p2", toComponentId = "bat", toPinId = "neg")
        )

        val result = CircuitEngine.solve(listOf(battery, led, resistor), wires, isPaused = false)
        assertEquals(SimulationState.OPEN_CIRCUIT, result.state)
        assertEquals(0f, result.totalCurrentMa, 0.001f)
        assertTrue(result.messageEn.contains("Reverse Bias") || result.messageBn.contains("রিভার্স"))
    }
}
