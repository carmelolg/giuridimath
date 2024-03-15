package it.carmelolagamba.giuridimath

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * @author carmelolg
 * @since version 0.1
 */
@HiltAndroidApp
class GiuridiMathApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
    }

}

