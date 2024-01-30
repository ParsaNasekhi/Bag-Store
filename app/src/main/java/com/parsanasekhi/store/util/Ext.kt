package com.parsanasekhi.store.util

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import java.text.SimpleDateFormat
import java.util.Calendar

val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->

    Log.e(TAG, "Error: ${throwable.message}")

}

fun stylePrice(price: String): String {
    var newPrice = ""
    if (price.length > 3) {
        val reversed = price.reversed()
        reversed.forEachIndexed { index, char ->
            newPrice += char
            if ((index + 1) % 3 == 0) newPrice += ','
        }
        newPrice = newPrice.reversed()
        return if (newPrice.first() == ',')
            "${newPrice.substring(1)} Tomans"
        else "$newPrice Tomans"
    }
    return "$price Tomans"
}

@SuppressLint("SimpleDateFormat")
fun styleTime(timeInMillies: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd hh:mm")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillies
    return formatter.format(calendar.time)
}