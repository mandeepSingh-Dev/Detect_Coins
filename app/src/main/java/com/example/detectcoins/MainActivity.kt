package com.example.detectcoins

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.number.IntegerWidth
import android.icu.number.NumberRangeFormatter
import android.media.AudioRecord
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity(),AudioCallback{


    lateinit var binding : ActivityMainBinding
    lateinit var audioCalculator : AudioCalculator

    lateinit var audioCalculatork : AudioCalculatork
     val audioRecorder: AudioRecorder by lazy{  AudioRecorder(this,this)  }

    val doubleMinValue = Int.MIN_VALUE.toDouble()
    val intMinValue = Int.MIN_VALUE

    var delay:Int = 0

    var _scope = MutableStateFlow<Job>(Job())
    val scopee : StateFlow<Job> get() = _scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        audioCalculator = AudioCalculator()
        audioCalculatork = AudioCalculatork()



        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.RECORD_AUDIO ),100)
        }


        CoroutineScope(Dispatchers.Main).launch {
          /* val state =  audioRecorder.start(applicationContext)
            when(state)
            {
             AudioRecord.STATE_UNINITIALIZED -> Snackbar.make(binding.root,"State Uninitialized !",Snackbar.LENGTH_SHORT).show()
             AudioRecord.RECORDSTATE_RECORDING -> Snackbar.make(binding.root,"Recording started", Snackbar.LENGTH_SHORT).show()
                AudioRecord.STATE_INITIALIZED -> Snackbar.make(binding.root,"State Uninitialized", Snackbar.LENGTH_SHORT).show()
                AudioRecord.RECORDSTATE_STOPPED -> Snackbar.make(binding.root,"Recording Stopped", Snackbar.LENGTH_SHORT).show()
            }*/
        }

        setListener()

        CoroutineScope(Dispatchers.Main).launch {
            scopee.collect {
                Toast.makeText(this@MainActivity, it.isActive.toString(), Toast.LENGTH_SHORT).show()            }
        }
    }

    fun setListener(){
        binding.resetBtn.setOnClickListener {

            delay = 500
           val job =  CoroutineScope(Dispatchers.Main).launch {
                if(maxAmplitude!=intMinValue && maxdecibel!= doubleMinValue && maxfrequency!=doubleMinValue && maxFreq_KHz!=doubleMinValue)
                {
                    maxAmplitude = intMinValue
                    maxdecibel = doubleMinValue
                    maxfrequency = doubleMinValue
                    maxFreq_KHz = doubleMinValue

                    binding.maxReadingsTextview.text = "Max. Amp 0 \n Max.dB 0.0 \n Max. Hz 0.0 \n Max. KHz 0.0"

                }else{ }
                delay(delay.toLong())
                delay = 0
           }
        }

        binding.stopListeningBtn.setOnClickListener {
            val recordingState = audioRecorder.getRecorderState()
            if(recordingState == AudioRecord.RECORDSTATE_RECORDING || recordingState == AudioRecord.STATE_INITIALIZED)
            {
                audioRecorder.stop()
            }else{
            }
        }

        binding.startListeningBtn.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                if (audioRecorder.recorder == null) {
                    Toast.makeText(this@MainActivity, "Recording Started", Toast.LENGTH_SHORT).show()
                    audioRecorder.start(this@MainActivity)
                }
                else{
                    val recordingState = audioRecorder.getRecorderState()
                    if( recordingState == AudioRecord.STATE_UNINITIALIZED || recordingState == AudioRecord.RECORDSTATE_STOPPED )
                    {
                            val state = audioRecorder.start(this@MainActivity)
                            if(state == AudioRecord.STATE_INITIALIZED || state == AudioRecord.RECORDSTATE_RECORDING)
                            {
                            }
                    }
                    else{
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode.equals(PackageManager.PERMISSION_GRANTED))
         {
             Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
         }
    }



    var maxAmplitude = intMinValue
    var maxdecibel : Double = doubleMinValue
    var maxfrequency : Double = doubleMinValue
    var maxFreq_KHz : Double = doubleMinValue


    @SuppressLint("SetTextI18n")
    override fun onBufferAvailable(buffer : ByteArray) {

        CoroutineScope(Dispatchers.Main).launch {
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

                if(delay == 0) {
                    binding.maxReadingsTextview.text = "Max. Amp ${maxAmplitude} \n Max.dB ${maxdecibel} \n Max. Hz ${maxfrequency} \n Max. KHz ${maxFreq_KHz}"
                }


            }
        }



        }

    override fun onStop() {
        super.onStop()
        if(audioRecorder != null)
        {
            audioRecorder.stop()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(audioRecorder!=null) {
            audioRecorder.stop()
        }
    }
}