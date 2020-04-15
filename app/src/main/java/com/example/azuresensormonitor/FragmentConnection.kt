package com.example.azuresensormonitor

import android.bluetooth.BluetoothClass
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import com.example.azuresensormonitor.databinding.FragmentConnectionBinding
import com.microsoft.azure.sdk.iot.device.DeviceClient
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol

class FragmentConnection : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    companion object {
        fun instance() = FragmentConnection()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)

        binding.connectButton.setOnClickListener {
            val input = binding.input.text.toString()
            if (!input.isBlank() && !input.any(setOf('(',')','<','>','@',',','.',';',':','"','\\','[',']','?','=','{','}',' ')::contains)) {
                viewModel._deviceId.value  = input
                viewModel.onConnectButtonClicked()
            } else {
                Toast.makeText(context, "Enter valid device id", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}