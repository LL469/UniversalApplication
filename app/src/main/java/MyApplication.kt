package com.abc.emptyactivity
import android.app.Application
import android.os.SystemClock
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

class MyApplication : Application() {

    lateinit var robot: Robot
    val locations = listOf("one", "two", "three")

    var speechSentences = listOf(
        "Tere tulemast Teadlaste ööle!",
        "Kuidas läheb?",
        "Sa näed hea välja!",
        "Paistad teadmushimuline?",
        "Mida sina leiutanud oled?",
        "Kas sa Teadusteatris käisid?",
        "Sinus on potentsiaali",
        "TalTech on sinu tulevik",
        "TalTechis saad reaalsed teadmised, et luua reaalseid asju",
        "TalTechis muudad maailma!",
        "Põnevat õhtut!",
    )

    var eligiblePlaces = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
    )

    fun speak(message: String, language: TtsRequest.Language) {
        robot.speak(TtsRequest.create(message, false, language, true, true))
    }

    var greetStatus = -1

    var previousLocation = 0
    var isRunning = false
    var loopIndex = 0

    var passedTime: Long = 0L
    var startTime: Long = 0L

    fun getTotalPassedTime(): Long {
        return if (isRunning) {
            passedTime + (SystemClock.elapsedRealtime() - startTime)
        } else {
            passedTime
        }
    }

    override fun onCreate() {
        super.onCreate()

        robot = Robot.getInstance()
    }
}