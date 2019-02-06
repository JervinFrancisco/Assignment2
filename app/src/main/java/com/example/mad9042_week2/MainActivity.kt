package com.example.mad9042_week2

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.EditText
import kotlin.math.max


class MainActivity : AppCompatActivity() {

    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null

    var maxLight = 0.0f
    var ambientlight=0.0f
    var currentX=0.0f

    var xInta = 0.0f
    var xBool = true

    var flashLightStatus = false
    var deviceHasCameraFlash: Boolean = false


    lateinit var camManager : CameraManager
    lateinit var cameraId : String

    lateinit var xEditText : EditText
    lateinit var yEditText : EditText
    lateinit var zEditText : EditText


    lateinit var screenBackground: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get the screen background so that you can change its color
        screenBackground = findViewById(R.id.screenBackground)



        // flashlight code  example.
        camManager= getSystemService(Context.CAMERA_SERVICE)  as CameraManager
        cameraId = camManager.cameraIdList[0] // Usually front camera is at 0 position.
        deviceHasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //Vibration example. Look at powerpoint slides on vibration
        val vibrateButton = findViewById<Button>(R.id.vibrate_button)
        val vibrateMotor = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if(currentX>(xInta-45) || currentX<(xInta+45)){
            val pattern = longArrayOf(500, 500, 500, 500)
            val amplitudes = intArrayOf(0, 255, 0, 128)
            vibrateMotor.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1) )
        }

        Log.i("X INta" , xInta.toString())

            val pattern = longArrayOf(500, 500, 500, 500)
            val amplitudes = intArrayOf(0, 255, 0, 128)
            vibrateMotor.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1) )



        //end of vibration example



        //sensor example. Look at powerpoint slides on sensor readings.
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Now get a sensor:
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        mSensorManager.registerListener(
            OrientationListener(), // look at line 125 for class declaration
            mSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )


        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mSensorManager.registerListener(
            AmbientLightListener(), // look at line 145 for class definition
            mSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        //load the edit text from the screen. We will write to them later.
        xEditText = findViewById(R.id.x_values)
        yEditText = findViewById(R.id.y_values)
        zEditText = findViewById(R.id.z_values)
    }

    inner class OrientationListener : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            val values = event.values

            val x = values[0]
            currentX = x
            Log.i("x axis", x.toString())
            val y = values[1]

            val vibrateMotor = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if(xBool == true){
                xInta =x
                xBool = false
            }

            
            if(y>-45&& ambientlight<100) {
                try {
                    if (deviceHasCameraFlash) {
                        if (!flashLightStatus) {//when light off

                            //turn the light On:
                            camManager.setTorchMode(cameraId, true)
                        }
                        //flip true to false, or false to true
                    }
                    flashLightStatus = !flashLightStatus
                } catch (e: Throwable) {
                    Log.i("Exception:", e.message)
                }
            }else if (y<0){
                try {
                    if (deviceHasCameraFlash) {
                        if (flashLightStatus) {//when light on

                            //turn the light Off:
                            camManager.setTorchMode(cameraId, false)
                        }
                    }

                    flashLightStatus = !flashLightStatus  //flip true to false, or false to true
                } catch (e: Throwable) {
                    Log.i("Exception:", e.message)
                }


            }



            xEditText.setText("X: $x")
            yEditText.setText("Y: $y")

        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code
        }
    }


    inner class AmbientLightListener : SensorEventListener
    {
        override fun onSensorChanged(event: SensorEvent) {
            val values = event.values

            val z = values[0]


            ambientlight=z
            Log.i("Light:", "Lux:"+ values[0])
            maxLight = Math.max(maxLight, values[0])
            zEditText.setText("Z: $z")

            if(z < 100 ){
                try {
                    if (deviceHasCameraFlash ) {
                        if (flashLightStatus) {//when light on

                            //turn the light Off:
                            camManager.setTorchMode(cameraId, false)
                        }
                    }
                } catch (e: Throwable) {
                    Log.i("Exception:", e.message)
                }
            }

        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code.
        }
    }

    //end of sensor example
}