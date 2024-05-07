package com.vivek.currencyconverter.utils

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 *
    To round off the decimal to 2 decimal places
 */
fun Double.roundOffDecimal(): Double {
    return String.format("%.2f", this).toDouble()
}



fun EditText.addDebouncedTextChangedListener(
    action: (String) -> Unit
) {
    // Internal debounce time and scope
    val debounceTime = 500L // Default debounce time (500 milliseconds)
    val coroutineScope = CoroutineScope(Dispatchers.Main) // Coroutine scope for UI operations

    var debounceJob: Job? = null

    this.doOnTextChanged { text, _, _, _ ->
        debounceJob?.cancel() // Cancel any existing job when text changes

        debounceJob = coroutineScope.launch {
            delay(debounceTime) // Wait for the default debounce time
            action(text?.toString() ?: "") // Execute the action with the updated text
        }
    }
}


fun shouldRefresh(
    currentTime: Long,
    lastRefreshTime: Long,
    thresholdMinutes: Int
): Boolean {
    // Calculate the time difference in minutes
    val timeDifference = currentTime - lastRefreshTime
    val thresholdMillis = TimeUnit.MINUTES.toMillis(thresholdMinutes.toLong())

    // Return true if the time difference is greater than the threshold
    return timeDifference > thresholdMillis
}
