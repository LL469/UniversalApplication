package com.abc.emptyactivity
import android.app.Application
import android.os.SystemClock
import com.robotemi.sdk.Robot

class MyApplication : Application() {

    lateinit var robot: Robot
    val locations = listOf("one", "two", "three")
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