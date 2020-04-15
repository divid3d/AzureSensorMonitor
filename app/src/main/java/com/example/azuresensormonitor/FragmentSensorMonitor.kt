package com.example.azuresensormonitor

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.azuresensormonitor.data.Acceleration
import com.example.azuresensormonitor.databinding.FragmentSensorMonitorBinding
import com.google.gson.JsonParser
import com.microsoft.azure.sdk.iot.device.*

class FragmentSensorMonitor : Fragment(), SensorEventListener {

    private var _binding: FragmentSensorMonitorBinding? = null
    private val binding get() = _binding!!

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var deviceClient: DeviceClient? = null

    private val viewModel: SharedViewModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    var lastSampleTime = 0L


    companion object {
        fun instance() = FragmentSensorMonitor()
        private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST
        val protocol = IotHubClientProtocol.MQTT
        const val connectionString =
            "HostName=MyCustomIoTCenter.azure-devices.net;DeviceId=AndroidSmartphone;SharedAccessKey=lxgOiHmJaYDIdiEYrB7VAmZwtSY/NKPVUXS6ZsNU5RQ="
        const val samplePeriodMs = 50
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        deviceClient = DeviceClient(connectionString, protocol)
        deviceClient?.open()

        sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        if (accelerometer == null) {
            Toast.makeText(
                context,
                getString(R.string.fragment__sensor_monitor__no_accelerometer_toast__text),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSensorMonitorBinding.inflate(inflater, container, false)
        binding.stopButton.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        deviceClient?.open()
        sensorManager?.registerListener(this, accelerometer, SENSOR_DELAY)
    }

    override fun onPause() {
        super.onPause()
        deviceClient?.closeNow()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                if ((event.timestamp - lastSampleTime) / 1_000_000 >= samplePeriodMs) {
                    sendAccelerometerData(event)
                    lastSampleTime = event.timestamp
                }
            }

        }
    }


    private fun sendAccelerometerData(event: SensorEvent) {
        with(event.values) {
            val acceleration =
                Acceleration(event.timestamp.getTimeString, this[0], this[1], this[2])
            updateAccelerometerDataUi(acceleration)
            Log.d("Accelerometer", acceleration.toString())
            sendData(acceleration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateAccelerometerDataUi(acceleration: Acceleration) {
        with(binding) {
            currentAccelerationX.text = acceleration.x.toString()
            currentAccelerationY.text = acceleration.y.toString()
            currentAccelerationZ.text = acceleration.z.toString()
            lastUpdated.text = acceleration.timestamp
        }
    }


    private fun sendData(data: Acceleration) {
        val dataObject = JsonParser().parse(data.serialize()).asJsonObject
        dataObject.addProperty(
            "device_id",
            viewModel.deviceid.value
        )
        val serializedData = dataObject.toString()
        val message = Message(serializedData)
        Log.d("Message sender", "Sending message: $serializedData")
        deviceClient?.sendEventAsync(
            message,
            { responseStatus, _ ->
                Log.d(
                    "IoT Hub response",
                    "Iot Hub responded to message with status: ${responseStatus.name}"
                )
            }, null
        )
    }

}