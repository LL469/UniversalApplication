package com.abc.emptyactivity

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robotemi.sdk.BatteryData
import com.robotemi.sdk.listeners.OnBatteryStatusChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener

class MainActivity : AppCompatActivity(), OnGoToLocationStatusChangedListener, OnBatteryStatusChangedListener {

    lateinit var app: MyApplication

    lateinit var bControl: Button
    lateinit var tvBattery: TextView
    lateinit var tvLoop: TextView
    lateinit var tvDescription: TextView
    lateinit var tvLocation: TextView
    lateinit var tvClock: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        app = application as MyApplication

        bControl = findViewById(R.id.bControl)
        tvBattery = findViewById(R.id.tvBattery)
        tvLoop = findViewById(R.id.tvLoop)
        tvDescription = findViewById(R.id.tvDescription)
        tvLocation = findViewById(R.id.tvLocation)
        tvClock = findViewById(R.id.tvClock)

        bControl.text = if (app.isRunning) "Stop" else "Start"
    }

    override fun onStart() {
        super.onStart()
        app.robot.addOnGoToLocationStatusChangedListener(this)
        app.robot.addOnBatteryStatusChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        app.robot.removeOnGoToLocationStatusChangedListener(this)
        app.robot.removeOnBatteryStatusChangedListener(this)
    }

    private fun start() {
        if (!app.isRunning) {
            app.startTime = SystemClock.elapsedRealtime()
            app.isRunning = true
            app.robot.batteryData?.let {
                tvBattery.text = "🔋: ${it.level}%"
            }
        }
        app.robot.patrol(app.locations, true, 0)
        log("start")
        bControl.text = "Stop"
    }

    private fun stop() {
        if (app.isRunning) {
            app.passedTime += SystemClock.elapsedRealtime() - app.startTime
            app.isRunning = false
        }
        app.robot.stopMovement()
        log("stop")
        bControl.text = "Start"
    }

    fun bControlOnClick(view: View) {
        if (app.isRunning) {
            stop()
        } else {
            start()
        }
    }

    private fun updateVariables() {
        app.loopIndex += 1
        tvLoop.text = "🔄: ${app.loopIndex}"
        log("loop " + app.loopIndex.toString())
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        if (status == "complete") {
            if (location == app.locations.lastOrNull()) {
                updateVariables()
            }

        }

        tvLocation.text = "📌: ${location}"
        val totalSeconds = app.getTotalPassedTime() / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        tvClock.text = "⏱️: %02d:%02d".format(minutes, seconds)
    }

    override fun onBatteryStatusChanged(batteryData: BatteryData?) {
        batteryData?.let {
            tvBattery.text = "🔋: ${it.level}%"
            log("battery " + it.level.toString())
        }
    }

    fun log(message: String) {
        Log.d("Mine", message)
    }
}