package com.example.template.features.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.template.core.ui.AppTheme
import com.example.template.core.ui.icons.AppIcons
import com.example.template.core.strings.R as StringsR

@Composable
internal fun DetailView(
  viewState: DetailViewState,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = { Text((viewState as? DetailViewState.Content)?.title.orEmpty()) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(AppIcons.ArrowBack, contentDescription = stringResource(StringsR.string.common_back))
          }
        },
      )
    },
  ) { contentPadding ->
    Box(Modifier.fillMaxSize().padding(contentPadding).padding(16.dp), contentAlignment = Alignment.TopStart) {
      when (viewState) {
        DetailViewState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
        DetailViewState.Missing -> Text(stringResource(StringsR.string.detail_missing), Modifier.align(Alignment.Center))
        is DetailViewState.Content -> Text(viewState.description, style = MaterialTheme.typography.bodyLarge)
      }
    }
  }
}

@Preview
@Composable
private fun DetailViewPreview() {
  AppTheme {
    DetailView(viewState = DetailViewState.Content(title = "Item 1", description = "The description of item 1."), onBack = {})
  }
}
