package com.example.azuresensormonitor.data

import com.google.gson.Gson

data class Acceleration(
    val timestamp: String,
    val x: Float,
    val y: Float,
    val z: Float
){
    fun serialize() = Gson().toJson(this)?: ""
}
