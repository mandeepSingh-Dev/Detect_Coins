package com.example.detectcoins.calculators

import android.util.Log
import kotlin.experimental.and
import kotlin.experimental.or

class AudioCalculatork {

    private var bytes : ByteArray?=null
    private var amplitudes : IntArray? = null
    private var decibels: DoubleArray? = null
    private var frequency : Double = 0.0
    private var amplitude : Int = 0
    private var decibel : Double = 0.0


    fun setBytes(bytess : ByteArray){
        bytes = bytess
        amplitudes = null
        decibels = null
        frequency = 0.0
        amplitude = 0
        decibel = 0.0

    }

    fun getAmplitudes(): IntArray? {
        if (amplitudes == null) {
            amplitudes = getAmplitudesFromBytes(bytes!!)
        }
            return amplitudes
    }

    private fun getAmplitudesFromBytes(bytes: ByteArray): IntArray? {
        val amps = IntArray(bytes.size / 2)
        var i = 0
        while (i < bytes.size) {

            var buff = bytes[i + 1].toShort()
            var buff2 = bytes[i].toShort()

            val buffshort =(buff and 0XFF)
            val buff2short = (buff2 and 0xFF)

            buff = buffshort.toInt().shl(8).toShort()
            buff2 = buff2short.toInt().toShort()

            val res: Short = (buff or  buff2).toShort()
            amps[if (i == 0) 0 else i / 2] = res.toInt()

            i += 2
        }
        Log.d("ampsyuyuyuy",amps.last().toString())
        return amps
    }

    fun getAmplitudeLevels(): IntArray? {
        if (amplitudes == null) getAmplitudes()
        var major = 0
        var minor = 0
        for (i in amplitudes!!) {
            if (i > major) major = i
            if (i < minor) minor = i
        }
        amplitude = Math.max(major, minor * -1)

        Log.d("ndsdsfds",amplitude.toString())
        return intArrayOf(major, minor)
    }

    fun getAmplitude(): Int {
        if (amplitude == 0) getAmplitudeLevels()
        return amplitude
    }


    fun getDecibel(): Double {
        if (decibel == 0.0) decibel = resizeNumber(getRealDecibel(amplitude))
        return decibel
    }

    fun resizeNumber(value: Double): Double {
        val temp = (value * 10.0).toInt()
        return temp / 10.0
    }

    private fun getRealDecibel(amplitude: Int): Double {
        var amplitude = amplitude
        if (amplitude < 0) amplitude *= -1
        var amp = amplitude.toDouble() / 32767.0 * 100.0
        if (amp == 0.0) {
            amp = 1.0
        }
        var decibel = Math.sqrt(100.0 / amp)
        decibel *= decibel
        if (decibel > 100.0) {
            decibel = 100.0
        }
        return (-1.0 * decibel + 1.0) / Math.PI
    }

    fun getFrequency(): Double {
        if (frequency == 0.0) frequency = retrieveFrequency()
        return frequency
    }

    private fun retrieveFrequency(): Double {
        val length = bytes!!.size / 2
        var sampleSize = 8192
        while (sampleSize > length) sampleSize = sampleSize shr 1
        val frequencyCalculator = FrequencyCalculator(sampleSize)
        frequencyCalculator.feedData(bytes, length)
        return resizeNumber(frequencyCalculator.freq)
    }


}