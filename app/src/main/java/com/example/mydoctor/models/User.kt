package com.example.mydoctor.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val age: String = "",
    val sexe: String = "",
    val antecedents: String = "",
    val globalRiskScore: Int = 28, // Default 28 as per mockup
    val createdAt: Long = System.currentTimeMillis()
)
