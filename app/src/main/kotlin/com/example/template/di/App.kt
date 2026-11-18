package com.example.template.di

import android.app.Application
import com.example.template.core.common.rootGraph
import dev.zacsweers.metro.createGraph

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    rootGraph = createGraph<AppGraph>()
  }
}
