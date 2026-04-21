package com.example.mydoctor.models

data class Measurement(
    val id: String = "",
    val userId: String = "",
    val type: String = "",         // "HEART_RATE", "COUGH_AUDIO"
    val resultValue: String = "",  // ex: "72", "85"
    val resultStatus: String = "", // ex: "Normal", "Sain"
    val timestamp: Long = System.currentTimeMillis()
)
