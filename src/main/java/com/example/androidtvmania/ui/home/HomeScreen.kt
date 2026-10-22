package com.example.androidtvmania.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.CompactCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.androidtvmania.R
import com.example.androidtvmania.data.Catalog
import com.example.androidtvmania.data.CatalogRow
import com.example.androidtvmania.data.StreamItem
import com.example.androidtvmania.ui.theme.Accent

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onStreamSelected: (StreamItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 48.dp),
    ) {
        item { HomeHeader() }
        items(Catalog.rows) { row ->
            CatalogRowList(row = row, onStreamSelected = onStreamSelected)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HomeHeader() {
    Column(modifier = Modifier.padding(start = 48.dp, end = 48.dp, top = 36.dp, bottom = 12.dp)) {
        Text(
            text = "TV Mania",
            style = MaterialTheme.typography.displaySmall,
            color = Accent,
        )
        Text(
            text = stringResource(R.string.catalog_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CatalogRowList(
    row: CatalogRow,
    onStreamSelected: (StreamItem) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = row.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp),
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .focusRestorer(),
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(row.items, key = { it.id }) { item ->
                StreamCard(item = item, onClick = { onStreamSelected(item) })
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun StreamCard(
    item: StreamItem,
    onClick: () -> Unit,
) {
    CompactCard(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .height(168.dp),
        image = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (item.protocol.label == "HLS") Color(0xFF1A3A5C) else Color(0xFF3A1A4A),
                                Color(0xFF0B0D12),
                            ),
                        ),
                    ),
            ) {
                Text(
                    text = item.protocol.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Accent,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                )
            }
        },
        title = {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
        },
        subtitle = {
            Text(
                text = item.subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
            )
        },
    )
}
