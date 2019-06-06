package com.example.miyamotonozomi.heart_rate_app



import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.DEFAULT_BOLD
import android.view.View
import android.view.WindowManager


class MainActivity : WearableActivity() , SensorEventListener {
    val TAG :String = MainActivity::class.java.name
    var set :Boolean = false
    var hb :Float= 100.0f
    var isDisp :Boolean = true
    var loopEngine :LoopEngine = LoopEngine()
    private lateinit var msensorManager: SensorManager
    private lateinit var backGround:View
    private var msensor:Sensor? = null
    private var heartratesensor:Sensor? = null


    public override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()

        msensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        text.textSize = 20.0f
        loopEngine.start()
        text.setTextColor(Color.argb(255,140,140,140))
        text.typeface = Typeface.create(DEFAULT_BOLD, BOLD)

    }

    private fun createParam(w: Int, h: Int):LinearLayout.LayoutParams{
        return LinearLayout.LayoutParams(w,h)
    }


    override fun onResume() {
        super.onResume()
        msensor = msensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        /*heartratesensor = msensorManager.getDefaultSensor(65562)*/
        msensorManager.registerListener(this,msensor,SensorManager.SENSOR_DELAY_NORMAL)
        /*sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL)*/
    }

    override fun onPause() {
        super.onPause()
        msensorManager.unregisterListener(this)
    }




    override fun onSensorChanged(event: SensorEvent) {
        if (set==false) text.textSize = 60.0f
            if (event.sensor.type == Sensor.TYPE_HEART_RATE){
                hb = event.values[0]
                text.text  = (" "+hb.toInt())
                set = true
                Log.d("MainActivity",""+hb.toInt())
             }else{
                Log.d(TAG,"Unknown sensor type")
            }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG,"onAccuracyChanged!!")
    }



    fun update() {
        if(set){
            if(isDisp){
                backGround.setBackgroundColor(Color.argb(80,231,232,226))
                text.textSize = 60.0f
                val res = resources
                val bitmap = BitmapFactory.decodeResource(res, R.drawable.heart)
                val bitmap2 = Bitmap.createScaledBitmap(bitmap, 250,250,false)
                HeartImage.setImageBitmap(bitmap2)

            }else{
                backGround.setBackgroundColor(Color.argb(10,231,232,226))
                text.textSize = 70.0f
                val res = resources
                val bitmap = BitmapFactory.decodeResource(res, R.drawable.heart)
                val bitmap2 = Bitmap.createScaledBitmap(bitmap, 300,300,false)
                HeartImage.setImageBitmap(bitmap2)
            }
        }
        isDisp = !isDisp
    }



    class LoopEngine : Handler() {
        private var isUpdate:Boolean = false

        fun start(){
            this.isUpdate = true
            handleMessage(Message())
        }
        fun stop(){
            this.isUpdate = false
        }

        override fun handleMessage(msg: Message?) {
            this.removeMessages(0)
            if (this.isUpdate){
                MainActivity().update()
                sendMessageDelayed(obtainMessage(0), (60/MainActivity().hb * 1000).toLong())
            }
        }

    }

}







