package it.insubria.accordi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import android.content.pm.PackageManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val pitchDetector = PitchDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Se non abbiamo il permesso, lo richiediamo
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            // Se abbiamo già il permesso, avviamo la rilevazione del pitch
            permissionToRecordAccepted = true
            pitchDetector.startPitchDetection()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecordAccepted) {
                    pitchDetector.startPitchDetection()
                }
            }
        }
        if(!permissionToRecordAccepted) finish()
    }

    internal fun updateTuningBar(deviation: Double) {
        findViewById<SeekBar>(R.id.tuningBar).progress = (50 + deviation * 10).roundToInt()

        if(deviation in -0.5..0.5)
            findViewById<ImageView>(R.id.tuningIndicator).visibility = ImageView.VISIBLE
        else
            findViewById<ImageView>(R.id.tuningIndicator).visibility = ImageView.INVISIBLE
    }
}