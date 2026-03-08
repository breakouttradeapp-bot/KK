package com.aikundli

import android.app.Application
import android.util.Log

class KundliApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupGlobalCrashHandler()
    }

    /**
     * Global last-resort crash handler — logs the crash so it shows in Logcat
     * and then lets the system handle cleanup normally.
     */
    private fun setupGlobalCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("KundliApp", "UNCAUGHT EXCEPTION on thread '${thread.name}'", throwable)
            } catch (_: Exception) { /* swallow logging errors */ }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
