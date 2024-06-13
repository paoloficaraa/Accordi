package it.insubria.accordi

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class DbHandler {
    companion object {
        private val database = FirebaseDatabase.getInstance().getReference()

        fun saveScale(scale: Scale, user: FirebaseUser) {
            database.child(user.uid).child("scala").child(scale.scale).setValue(scale.notes)
        }

        suspend fun getScales(user: FirebaseUser) : List<Scale> {
            val scales = mutableListOf<Scale>()
            val snapshot = database.child(user.uid).child("scala").get().await()
                for (scale in snapshot.children) {
                    val notes = mutableListOf<Note>()
                    for (note in scale.children) {
                        notes.add(Note(note.child("note").value.toString(), note.child("octave").value.toString(), note.child("frequency").value.toString().toDouble()))
                    }
                    scales.add(Scale(notes, scale.key.toString()))
                }
            return scales
        }
    }
}