package it.insubria.accordi

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

class NoteHandler() {
    companion object {
        fun getNoteAndDeviationFromFrequency(frequency: Double): Pair<Note , Double> {
            val notes = arrayOf(
                "DO" ,
                "DO#" ,
                "RE" ,
                "RE#" ,
                "MI" ,
                "FA" ,
                "FA#" ,
                "SOL" ,
                "SOL#" ,
                "LA" ,
                "LA#" ,
                "SI"
            )
            val noteIndex = round((12 * ln(frequency / 440.0) / ln(2.0)) + 69).toInt()
            val note = notes[noteIndex % 12]
            val octave = noteIndex / 12 - 1
            val deviation = frequency - 440.0 * 2.0.pow((noteIndex - 69).toDouble() / 12.0)
            return Pair(Note(note, octave.toString(), frequency) , deviation)
        }

        fun getScale(notes: List<String>): String {
            return when {
                notes.containsAll(listOf("DO", "RE", "MI", "FA", "SOL", "LA", "SI")) -> "DO maggiore"
                notes.containsAll(listOf("DO#", "RE#", "MI", "FA#", "SOL#", "LA#", "SI")) -> "DO# maggiore"
                notes.containsAll(listOf("RE", "MI", "FA#", "SOL", "LA", "SI", "DO#")) -> "RE maggiore"
                notes.containsAll(listOf("RE#", "MI#", "FA#", "SOL#", "LA#", "SI", "DO#")) -> "RE# maggiore"
                notes.containsAll(listOf("MI", "FA#", "SOL#", "LA", "SI", "DO#", "RE#")) -> "MI maggiore"
                notes.containsAll(listOf("FA", "SOL", "LA", "SIb", "DO", "RE", "MI")) -> "FA maggiore"
                notes.containsAll(listOf("FA#", "SOL#", "LA#", "SI", "DO#", "RE#", "MI#")) -> "FA# maggiore"
                notes.containsAll(listOf("SOL", "LA", "SI", "DO", "RE", "MI", "FA#")) -> "SOL maggiore"
                notes.containsAll(listOf("SOL#", "LA#", "SI", "DO#", "RE#", "MI#", "FA#")) -> "SOL# maggiore"
                notes.containsAll(listOf("LA", "SI", "DO#", "RE", "MI", "FA#", "SOL#")) -> "LA maggiore"
                notes.containsAll(listOf("LA#", "SI", "DO#", "RE#", "MI#", "FA#", "SOL#")) -> "LA# maggiore"
                notes.containsAll(listOf("SI", "DO#", "RE#", "MI", "FA#", "SOL#", "LA#")) -> "SI maggiore"

                notes.containsAll(listOf("DO", "RE", "RE#", "FA", "SOL", "LA", "LA#")) -> "DO minore"
                notes.containsAll(listOf("DO#", "RE#", "MI", "FA#", "SOL#", "LA#", "LA#")) -> "DO# minore"
                notes.containsAll(listOf("RE", "MI", "FA", "SOL", "LA", "SI", "DO")) -> "RE minore"
                notes.containsAll(listOf("RE#", "MI#", "FA#", "SOL#", "LA#", "SI", "DO#")) -> "RE# minore"
                notes.containsAll(listOf("MI", "FA#", "SOL", "LA", "SI", "DO#", "RE")) -> "MI minore"
                notes.containsAll(listOf("FA", "SOL", "LA", "LA#", "DO", "RE", "RE#")) -> "FA minore"
                notes.containsAll(listOf("FA#", "SOL#", "LA#", "SI", "DO#", "RE#", "MI")) -> "FA# minore"
                notes.containsAll(listOf("SOL", "LA", "SI", "DO", "RE", "MI", "FA")) -> "SOL minore"
                notes.containsAll(listOf("SOL#", "LA#", "SI", "DO#", "RE#", "MI#", "FA#")) -> "SOL# minore"
                notes.containsAll(listOf("LA", "SI", "DO#", "RE", "MI", "FA#", "SOL")) -> "LA minore"
                notes.containsAll(listOf("LA#", "SI", "DO#", "RE#", "MI#", "FA#", "SOL")) -> "LA# minore"
                notes.containsAll(listOf("SI", "DO#", "RE#", "MI", "FA#", "SOL#", "LA")) -> "SI minore"

                else -> "Non riconosciuto"
            }
        }
    }
}