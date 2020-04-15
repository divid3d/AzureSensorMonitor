package com.example.azuresensormonitor

import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean

class SharedViewModel : ViewModel() {

    private val _navigateToSensorMonitorFragment = MutableLiveData<Event<Unit>>()
    val navigateToSensorMonitorFragment: LiveData<Event<Unit>>
        get() = _navigateToSensorMonitorFragment

    fun onConnectButtonClicked() {
        _navigateToSensorMonitorFragment.value = Event(Unit)
    }

    var _deviceId = MutableLiveData<String>()
    val deviceid: LiveData<String>
        get() = _deviceId

}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}