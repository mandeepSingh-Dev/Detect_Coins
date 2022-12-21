package com.example.detectcoins

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.number.IntegerWidth
import android.icu.number.NumberRangeFormatter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.detectcoins.calculators.AudioCalculator
import com.example.detectcoins.calculators.AudioCalculatork
import com.example.detectcoins.core.AudioCallback
import com.example.detectcoins.core.AudioRecorder
import com.example.detectcoins.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),AudioCallback{


    lateinit var binding : ActivityMainBinding
    lateinit var audioCalculator : AudioCalculator

    lateinit var audioCalculatork : AudioCalculatork
    lateinit var audioRecorder: AudioRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        audioCalculator = AudioCalculator()
        audioCalculatork = AudioCalculatork()

        Log.d("doidvmdv",Int.MAX_VALUE.toString()+"  MAX_VALUE")
        Log.d("doidvmdv",Int.MIN_VALUE.toString()+"  MIN_VALUE")
        Log.d("doidvmdv",Int.SIZE_BITS.toString()+"  SIZE_BITS")
        Log.d("doidvmdv",Int.SIZE_BYTES.toString()+"  SIZE_BITS")

        Log.d("doidvmdv",Double.MAX_VALUE.toString()+"  MAX_VALUE")
        Log.d("doidvmdv",Double.MIN_VALUE.toString()+"  MIN_VALUE")
        Log.d("doidvmdv",Double.SIZE_BITS.toString()+"  SIZE_BITS")
        Log.d("doidvmdv",Double.NEGATIVE_INFINITY.toString()+"  SIZE_BYTES")



        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.RECORD_AUDIO ),100)
        }

         audioRecorder = AudioRecorder(this)

        CoroutineScope(Dispatchers.Main).launch {
            audioRecorder.start(applicationContext)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode.equals(PackageManager.PERMISSION_GRANTED))
         {
             Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
         }
    }



    var maxAmplitude = 0
    var maxdecibel : Double = 0.0
    var maxfrequency : Double = 0.0
    var maxFreq_KHz : Double = 0.0


    @SuppressLint("SetTextI18n")
    override fun onBufferAvailable(buffer : ByteArray) {
        runOnUiThread {

           // binding.scrollview.fullScroll(View.FOCUS_DOWN)
            audioCalculatork.setBytes(buffer)
            audioCalculator.setBytes(buffer)
            Log.d("diofdfd",audioCalculatork.getAmplitude().toString())
            Log.d("dnskdJAVA",audioCalculator.getAmplitude().toString())
            binding.bytesTextView.setText( "AMP : ${ audioCalculatork.getAmplitude().toString()} \n" +
                                           "dB : ${audioCalculatork.getDecibel().toString()} \n " +
                                           "Hz : ${audioCalculatork.getFrequency().toString()}")


            if(audioCalculatork.getAmplitude() > maxAmplitude)
            {
                maxAmplitude = audioCalculatork.getAmplitude()
            }
            if(audioCalculatork.getDecibel() > maxdecibel)
            {
                maxdecibel = audioCalculatork.getDecibel()
            }
            if(audioCalculatork.getFrequency() > maxfrequency) {
                maxfrequency = audioCalculatork.getFrequency()
                maxFreq_KHz = maxfrequency/1000
            }

            binding.maxReadingsTextview.text = "Max. Amp ${maxAmplitude} \n Max.dB ${maxdecibel} \n Max. Hz ${maxfrequency} \n Max. KHz ${maxFreq_KHz}"









           // Log.d("fidfndifvnv",audioCalculator.getDecibel().toString())
            //Log.d("fidfndifvnv",audioCalculator.getFrequency().toString())

        }
        }

    override fun onDestroy() {
        super.onDestroy()
        audioRecorder.stop()
    }
}