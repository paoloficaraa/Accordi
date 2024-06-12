package it.insubria.accordi

import android.app.Application
import com.google.firebase.auth.FirebaseUser

class App : Application() {
    companion object {
        var utente: FirebaseUser? = null
    }
}