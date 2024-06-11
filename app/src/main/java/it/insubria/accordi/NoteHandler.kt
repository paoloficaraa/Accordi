package it.insubria.accordi

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

class NoteHandler() {

    fun getNoteAndDeviationFromFrequency(frequency: Double): Pair<String, Double> {
        val notes = arrayOf("DO", "DO#", "RE", "RE#", "MI", "FA", "FA#", "SOL", "SOL#", "LA", "LA#", "SI")
        val noteIndex = round((12 * ln(frequency / 440.0) / ln(2.0)) + 69).toInt()
        val note = notes[noteIndex % 12]
        val octave = noteIndex / 12 - 1
        val deviation = frequency - 440.0 * 2.0.pow((noteIndex - 69).toDouble() / 12.0)
        return Pair(note + octave, deviation)
    }
}