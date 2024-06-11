package it.insubria.accordi

import android.widget.TextView
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor

//costanti per inizializzazione dell'AudioDispatcher
const val SAMPLE_RATE = 44100
const val BUFFER_SIZE = 2048
const val OVERLAP = 1024

//costanti per far in modo che le note troppo vicine non vengano considerate
const val REQUIRED_CONSECUTIVE_NOTES = 2
const val DETECTION_INTERVAL_MS = 500

class PitchDetector(private val context: MainActivity) {
    //attributi per la gestione delle note
    private val noteHandler = NoteHandler()
    private val notes = mutableListOf<Note>() //lista delle note rilevate
    private var lastNote: String? = null //ultima nota rilevata
    @Volatile private var consecutiveNoteCount = 0 //contatore delle note consecutive
    @Volatile private var lastDetectionTime = System.currentTimeMillis() //tempo dell'ultima rilevazione

    //funzione per iniziare la rilevazione del pitch
    fun startPitchDetection() {
        val dispatcher = createAudioDispatcher()
        val pitchProcessor = createPitchProcessor()
        dispatcher.addAudioProcessor(pitchProcessor)
        Thread(dispatcher, "Audio Dispatcher").start()
    }

    //funzione per creare l'AudioDispatcher
    private fun createAudioDispatcher(): AudioDispatcher =
        AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP)

    //funzione per creare il PitchProcessor
    private fun createPitchProcessor(): PitchProcessor {
        val pitchDetectionHandler = PitchDetectionHandler { res, _ ->
            if (res.isPitched) {
                handlePitchedResult(res)
            }
        }

        return PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            SAMPLE_RATE.toFloat(),
            BUFFER_SIZE,
            pitchDetectionHandler
        )
    }

    //funzione per gestire il risultato della rilevazione del pitch
    private fun handlePitchedResult(res: PitchDetectionResult) {
        context.runOnUiThread {
            val (note, deviation) = noteHandler.getNoteAndDeviationFromFrequency(res.pitch.toDouble())
            updateNoteAndDetectionTime(note)
            if (isConsecutiveNote()) {
                handleConsecutiveNote(note, res.pitch, deviation)
            }
        }
    }

    //funzione per aggiornare l'ultima nota rilevata e il tempo dell'ultima rilevazione così da non contare una nota sbagliata
    private fun updateNoteAndDetectionTime(note: Note) {
        synchronized(this) {
            if (lastNote == null || lastNote != note.note) {
                consecutiveNoteCount = 0
                lastNote = note.note
                lastDetectionTime = System.currentTimeMillis()
            } else if (System.currentTimeMillis() - lastDetectionTime > DETECTION_INTERVAL_MS) {
                consecutiveNoteCount++
                lastDetectionTime = System.currentTimeMillis()
            }
        }
    }


    private fun isConsecutiveNote() = consecutiveNoteCount >= REQUIRED_CONSECUTIVE_NOTES

    //funzione che aggiunge alla lista delle note la nota corrente e che poi modifica la UI
    private fun handleConsecutiveNote(note: Note, pitch: Float, deviation: Double) {
        synchronized(this) {
            if (notes.isEmpty() || notes.last().note != note.note) {
                notes.add(note)
            }

            updateUI(note.toString(), pitch, deviation)

            if (notes.size == 7) {
                val scale = noteHandler.getScale(notes.map { it.note })
                val scaleTv = context.findViewById<TextView>(R.id.ScaleTv)
                scaleTv.text = context.getString(R.string.scale_text, scale)
                scaleTv.visibility = TextView.VISIBLE
                notes.clear()
            }

            consecutiveNoteCount = 0
        }
    }

    private fun updateUI(note: String, pitch: Float, deviation: Double) {
        context.findViewById<TextView>(R.id.noteTextView).text = context.getString(R.string.note_text, note)
        context.findViewById<TextView>(R.id.frequencyTextView).text = context.getString(R.string.frequency_text, pitch)
        context.updateTuningBar(deviation)
    }
}
