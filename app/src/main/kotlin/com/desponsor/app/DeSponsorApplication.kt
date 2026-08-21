package com.desponsor.app

import android.app.Application

class DeSponsorApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
