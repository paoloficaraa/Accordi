package it.insubria.accordi

import android.widget.TextView
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor

class PitchDetector(private val context: MainActivity) {
    private val noteHandler = NoteHandler()
    private val notes = mutableListOf<Note>()
    private var lastNote: String? = null
    private var consecutiveNoteCount = 0
    private val requiredConsecutiveNotes = 3
    private var lastDetectionTime = System.currentTimeMillis()

    fun startPitchDetection() {
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 4096, 2048)

        val pitchDetectionHandler = PitchDetectionHandler { res, _ ->
            if (res.isPitched) {
                context.runOnUiThread {
                    val (note, deviation) = noteHandler.getNoteAndDeviationFromFrequency(res.pitch.toDouble())

                    // Check if the note is the same as the last one detected
                    if (lastNote == null || lastNote != note.note) {
                        // Different note detected, reset the counter
                        consecutiveNoteCount = 0
                        lastNote = note.note
                        lastDetectionTime = System.currentTimeMillis()
                    } else if (System.currentTimeMillis() - lastDetectionTime > 500){
                        // Same note detected, increment the counter
                        consecutiveNoteCount++
                        lastDetectionTime = System.currentTimeMillis()
                    }

                    if (consecutiveNoteCount >= requiredConsecutiveNotes) {
                        if(notes.isEmpty() || notes.last().note != note.note)
                            notes.add(note)

                        updateUI(note.toString(), res.pitch, deviation)

                        if (notes.size == 7) {
                            println(notes)
                            val scale = noteHandler.getScale(notes.map { it.note })
                            println("Scala di: $scale")
                            notes.clear()
                        }

                        // Reset counter to avoid immediate repeats
                        consecutiveNoteCount = 0
                    }
                }
            }
        }

        val pitchProcessor = PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            44100F,
            4096,
            pitchDetectionHandler
        )
        dispatcher.addAudioProcessor(pitchProcessor)
        Thread(dispatcher, "Audio Dispatcher").start()
    }

    private fun updateUI(note: String, pitch: Float, deviation: Double) {
        context.findViewById<TextView>(R.id.noteTextView).text = "Nota: $note"
        context.findViewById<TextView>(R.id.frequencyTextView).text = "Frequenza: ${pitch} Hz"
        context.updateTuningBar(deviation)
    }
}
