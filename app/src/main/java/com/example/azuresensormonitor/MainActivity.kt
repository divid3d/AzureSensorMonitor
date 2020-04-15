package com.example.azuresensormonitor

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.azuresensormonitor.databinding.ScreenMainBinding
import com.microsoft.azure.sdk.iot.device.DeviceClient

class MainActivity : BaseActivity() {

    private val binding by viewBinding(ScreenMainBinding::inflate)
    val viewModel: SharedViewModel by lazy {
        ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        replaceFragment(FragmentConnection.instance())

        viewModel.navigateToSensorMonitorFragment.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                replaceFragment(FragmentSensorMonitor.instance())
            }
        })

    }
}