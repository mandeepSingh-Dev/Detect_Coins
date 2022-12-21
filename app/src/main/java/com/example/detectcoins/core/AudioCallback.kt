package com.example.detectcoins.core

interface AudioCallback {

    fun onBufferAvailable(bytes : ByteArray)
}