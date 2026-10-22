package com.example.androidtvmania.ui.player

import android.view.KeyEvent as AndroidKeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.androidtvmania.R
import com.example.androidtvmania.data.StreamItem
import com.example.androidtvmania.ui.theme.Accent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val SeekMs = 10_000L

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerScreen(
    stream: StreamItem,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val player = remember(stream.id) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(stream.url))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }

    var controlsVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    val playButtonFocus = remember { FocusRequester() }

    LaunchedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        try {
            while (isActive) {
                position = player.currentPosition.coerceAtLeast(0L)
                duration = player.duration.coerceAtLeast(0L)
                delay(400)
            }
        } finally {
            player.removeListener(listener)
        }
    }

    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(4_000)
            controlsVisible = false
        }
    }

    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            playButtonFocus.requestFocus()
        }
    }

    fun showControls() {
        controlsVisible = true
    }

    fun togglePlay() {
        if (player.isPlaying) player.pause() else player.play()
        showControls()
    }

    fun seekBy(delta: Long) {
        val target = (player.currentPosition + delta).coerceIn(0L, player.duration.coerceAtLeast(0L))
        player.seekTo(target)
        showControls()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.Back, Key.Escape -> {
                        onBack()
                        true
                    }
                    Key.DirectionLeft -> {
                        seekBy(-SeekMs)
                        true
                    }
                    Key.DirectionRight -> {
                        seekBy(SeekMs)
                        true
                    }
                    Key.DirectionCenter, Key.Enter, Key.Spacebar -> {
                        if (!controlsVisible) {
                            showControls()
                            true
                        } else {
                            false
                        }
                    }
                    else -> {
                        val native = event.nativeKeyEvent
                        if (native.keyCode == AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                            native.keyCode == AndroidKeyEvent.KEYCODE_MEDIA_PLAY ||
                            native.keyCode == AndroidKeyEvent.KEYCODE_MEDIA_PAUSE
                        ) {
                            togglePlay()
                            true
                        } else {
                            false
                        }
                    }
                }
            }
            .focusable(),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    useController = false
                    keepScreenOn = true
                }
            },
            update = { it.player = player },
        )

        AnimatedVisibility(
            visible = controlsVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            PlayerControls(
                stream = stream,
                isPlaying = isPlaying,
                position = position,
                duration = duration,
                playFocusRequester = playButtonFocus,
                onPlayPause = ::togglePlay,
                onRewind = { seekBy(-SeekMs) },
                onForward = { seekBy(SeekMs) },
            )
        }
    }
}

@kotlin.OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PlayerControls(
    stream: StreamItem,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    playFocusRequester: FocusRequester,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
) {
    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                ),
            )
            .padding(horizontal = 48.dp, vertical = 28.dp),
    ) {
        Text(text = stream.title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${stream.protocol.label}  ·  ${stream.subtitle}",
            style = MaterialTheme.typography.bodyMedium,
            color = Accent,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.2f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(Accent),
            )
        }
        Text(
            text = "${formatTime(position)} / ${formatTime(duration)}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onRewind, modifier = Modifier.widthIn(min = 120.dp)) {
                Text(stringResource(R.string.rewind))
            }
            Button(
                onClick = onPlayPause,
                modifier = Modifier
                    .focusRequester(playFocusRequester)
                    .widthIn(min = 120.dp),
            ) {
                Text(if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play))
            }
            Button(onClick = onForward, modifier = Modifier.widthIn(min = 120.dp)) {
                Text(stringResource(R.string.forward))
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
