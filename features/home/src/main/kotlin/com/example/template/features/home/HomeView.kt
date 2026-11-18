package com.example.template.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.template.core.ui.AppTheme
import com.example.template.core.strings.R as StringsR

@Composable
internal fun HomeView(
  viewState: HomeViewState,
  onItemClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeTopAppBar(title = { Text(stringResource(StringsR.string.home_title)) }, scrollBehavior = scrollBehavior)
    },
  ) { contentPadding ->
    when (viewState) {
      HomeViewState.Loading -> Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      is HomeViewState.Content -> if (viewState.items.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
          Text(stringResource(StringsR.string.home_empty))
        }
      } else {
        LazyColumn(contentPadding = contentPadding) {
          items(viewState.items, key = { it.id }) { row ->
            ListItem(
              headlineContent = { Text(row.title) },
              supportingContent = { Text(row.description) },
              modifier = Modifier.clickable { onItemClick(row.id) },
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun HomeViewPreview() {
  AppTheme {
    HomeView(
      viewState = HomeViewState.Content(
        items = List(5) { HomeViewState.Row(id = "$it", title = "Item $it", description = "The description of item $it.") },
      ),
      onItemClick = {},
    )
  }
}
