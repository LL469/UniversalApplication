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
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnBatteryStatusChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnGreetModeStateChangedListener

class MainActivity : AppCompatActivity(), OnGoToLocationStatusChangedListener, OnGreetModeStateChangedListener {

    lateinit var app: MyApplication

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

    }

    override fun onStart() {
        super.onStart()
        app.robot.addOnGoToLocationStatusChangedListener(this)
        app.robot.addOnGreetModeStateChangedListener(this)
        app.robot.hideTopBar(true)
    }

    override fun onStop() {
        super.onStop()
        app.robot.removeOnGoToLocationStatusChangedListener(this)
        app.robot.removeOnGreetModeStateChangedListener(this)
    }

    fun randomPlace(): String {
        val listLength = app.eligiblePlaces.size
        val randomLocation = (1..listLength).random()

        return if (kotlin.math.abs(randomLocation - app.previousLocation) <= 1) {
            randomPlace()
        } else {
            app.eligiblePlaces.entries
                .first { it.value == randomLocation }
                .key
        }
    }

    fun returnRandomSentence(): Int {
        return app.speechSentences.indices.random()
    }

    fun route() {
        app.robot.goTo(randomPlace())
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        when (status) {
            "complete", "abort" -> {
                app.previousLocation = app.eligiblePlaces[location]!!

                if (app.greetStatus != 4) {
                    route()
                }
            }
            else -> {

            }
        }
    }

    override fun onGreetModeStateChanged(state: Int) {
        app.greetStatus = state
        when (state) {
            1 -> {
                route()
            }
            4 -> app.speak(app.speechSentences[returnRandomSentence()], TtsRequest.Language.ET_EE)
            else -> {

            }
        }
    }
}