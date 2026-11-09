package com.example.engine

import com.example.model.CircuitComponent
import com.example.model.CircuitWire

data class CircuitSnapshot(
    val components: List<CircuitComponent>,
    val wires: List<CircuitWire>
)

class CircuitHistoryManager(private val maxHistory: Int = 30) {
    private val undoStack = ArrayDeque<CircuitSnapshot>()
    private val redoStack = ArrayDeque<CircuitSnapshot>()

    fun pushState(components: List<CircuitComponent>, wires: List<CircuitWire>) {
        val snapshot = CircuitSnapshot(
            components = components.map { it.copy() },
            wires = wires.map { it.copy() }
        )
        undoStack.addLast(snapshot)
        if (undoStack.size > maxHistory) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    fun undo(currentComponents: List<CircuitComponent>, currentWires: List<CircuitWire>): CircuitSnapshot? {
        if (undoStack.isEmpty()) return null
        val current = CircuitSnapshot(
            components = currentComponents.map { it.copy() },
            wires = currentWires.map { it.copy() }
        )
        redoStack.addLast(current)
        return undoStack.removeLast()
    }

    fun redo(currentComponents: List<CircuitComponent>, currentWires: List<CircuitWire>): CircuitSnapshot? {
        if (redoStack.isEmpty()) return null
        val current = CircuitSnapshot(
            components = currentComponents.map { it.copy() },
            wires = currentWires.map { it.copy() }
        )
        undoStack.addLast(current)
        return redoStack.removeLast()
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()
}
