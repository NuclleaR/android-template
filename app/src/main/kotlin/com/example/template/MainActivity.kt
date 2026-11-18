package com.example.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.template.core.common.rootGraphAs
import com.example.template.core.ui.AppTheme
import com.example.template.navigation.Destination
import com.example.template.navigation.NavEntryResolver
import com.example.template.navigation.NavigationCommand
import com.example.template.navigation.Navigator
import com.example.template.navigation.startDestinations
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface MainActivityGraph {
  val navigator: Navigator
  val navEntryResolver: NavEntryResolver
}

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val graph = rootGraphAs<MainActivityGraph>()
    setContent {
      val start = remember { startDestinations(this, intent).toTypedArray() }

      @Suppress("UNCHECKED_CAST")
      val backStack = rememberNavBackStack(*start) as MutableList<Destination>
      AppTheme {
        NavDisplay(
          backStack = backStack,
          onBack = {
            if (backStack.size > 1) backStack.removeLastOrNull()
          },
          entryProvider = { key -> graph.navEntryResolver.create(key) },
        )
      }
      LaunchedEffect(graph.navigator) {
        graph.navigator.navigationCommands.collect { command ->
          when (command) {
            is NavigationCommand.GoTo -> if (backStack.lastOrNull() != command.destination) backStack += command.destination
            NavigationCommand.GoBack -> if (backStack.size > 1) backStack.removeLastOrNull()
          }
        }
      }
    }
  }
}
