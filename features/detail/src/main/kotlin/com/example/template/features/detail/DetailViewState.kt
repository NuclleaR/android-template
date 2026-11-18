package com.example.template.features.detail

sealed interface DetailViewState {
  data object Loading : DetailViewState
  data object Missing : DetailViewState
  data class Content(
    val title: String,
    val description: String,
  ) : DetailViewState
}
