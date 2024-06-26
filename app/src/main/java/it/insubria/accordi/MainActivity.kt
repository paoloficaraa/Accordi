package it.insubria.accordi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val pitchDetector = PitchDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v , insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left , systemBars.top , systemBars.right , systemBars.bottom)
            insets
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Se non abbiamo il permesso, lo richiediamo
            ActivityCompat.requestPermissions(this , permissions , REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            // Se abbiamo già il permesso, avviamo la rilevazione del pitch
            permissionToRecordAccepted = true
            pitchDetector.startPitchDetection()
        }

        val btnStartRecognition = findViewById<TextView>(R.id.startRecognitionBtn)
        val btnStopRecognition = findViewById<TextView>(R.id.stopRecognitionBtn)
        val btnClearNotes = findViewById<Button>(R.id.clearNotesBtn)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegistration)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnScaleList = findViewById<Button>(R.id.btnScaleList)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        btnClearNotes.setOnClickListener {
            pitchDetector.clearNotes()
            Toast.makeText(this , "Note rimosse" , Toast.LENGTH_SHORT).show()
        }

        btnStartRecognition.setOnClickListener {
            pitchDetector.scaleRecognition = true
            btnClearNotes.visibility = Button.VISIBLE
            Toast.makeText(this , "Riconoscimento scala attivato" , Toast.LENGTH_SHORT).show()
        }

        btnStopRecognition.setOnClickListener {
            pitchDetector.scaleRecognition = false
            findViewById<TextView>(R.id.ScaleTv).visibility = TextView.INVISIBLE
            btnClearNotes.visibility = Button.INVISIBLE
            Toast.makeText(this , "Riconoscimento scala disattivato" , Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this , LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this , Registration::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            App.user = null
            btnLogout.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnRegister.visibility = View.VISIBLE
        }

        btnBackToMain.setOnClickListener {
            findViewById<FrameLayout>(R.id.fragment_container).visibility = View.GONE
            btnBackToMain.visibility = View.GONE
            findViewById<RelativeLayout>(R.id.noteCard).visibility = View.VISIBLE
            findViewById<SeekBar>(R.id.tuningBar).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.tuningIndicator).visibility = View.VISIBLE
            findViewById<TextView>(R.id.ScaleTv).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.layoutScaleButtons).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.layoutButtons2).visibility = View.VISIBLE
            btnLogout.visibility = View.VISIBLE
            btnScaleList.visibility = View.VISIBLE
        }

        if (App.user != null) {
            btnLogout.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
            btnRegister.visibility = View.GONE
            btnScaleList.visibility = View.VISIBLE
        } else {
            btnLogout.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnRegister.visibility = View.VISIBLE
            btnScaleList.visibility = View.GONE
        }

        btnScaleList.setOnClickListener {
            findViewById<RelativeLayout>(R.id.noteCard).visibility = View.GONE
            findViewById<SeekBar>(R.id.tuningBar).visibility = View.GONE
            findViewById<ImageView>(R.id.tuningIndicator).visibility = View.GONE
            findViewById<TextView>(R.id.ScaleTv).visibility = View.GONE
            findViewById<LinearLayout>(R.id.layoutScaleButtons).visibility = View.GONE
            btnClearNotes.visibility = View.GONE
            findViewById<LinearLayout>(R.id.layoutButtons2).visibility = View.VISIBLE
            btnLogout.visibility = View.GONE
            btnScaleList.visibility = View.GONE

            findViewById<FrameLayout>(R.id.fragment_container).visibility = View.VISIBLE
            btnBackToMain.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container , ScaleListFragment()).commit()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int , permissions: Array<out String> , grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecordAccepted) {
                    pitchDetector.startPitchDetection()
                }
            }
        }
        if (!permissionToRecordAccepted) finish()
    }

    internal fun updateTuningBar(deviation: Double) {
        findViewById<SeekBar>(R.id.tuningBar).progress = (50 + deviation * 10).roundToInt()

        if (deviation in -0.5..0.5) findViewById<ImageView>(R.id.tuningIndicator).visibility =
            ImageView.VISIBLE
        else findViewById<ImageView>(R.id.tuningIndicator).visibility = ImageView.INVISIBLE
    }
}