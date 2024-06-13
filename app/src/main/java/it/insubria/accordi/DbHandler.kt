package it.insubria.accordi

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class DbHandler {
    companion object {
        private val database = FirebaseDatabase.getInstance().getReference()

        fun saveScale(scale: Scala , utente: FirebaseUser) {
            database.child(utente.uid).child("scala").child(scale.scale).setValue(scale.notes)
        }
    }
}