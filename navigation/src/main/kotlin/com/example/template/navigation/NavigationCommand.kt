package com.example.template.navigation

sealed interface NavigationCommand {
  data class GoTo(val destination: Destination) : NavigationCommand
  data object GoBack : NavigationCommand
}
