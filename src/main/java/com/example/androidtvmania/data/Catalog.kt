package com.example.androidtvmania.data

enum class StreamProtocol(val label: String) {
    HLS("HLS"),
    DASH("DASH"),
}

data class StreamItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val url: String,
    val protocol: StreamProtocol,
)

data class CatalogRow(
    val title: String,
    val items: List<StreamItem>,
)

object Catalog {
    val featuredHls = listOf(
        StreamItem(
            id = "hls-mux",
            title = "Mux Demo",
            subtitle = "Adaptive HLS · Big Buck Bunny",
            url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            protocol = StreamProtocol.HLS,
        ),
        StreamItem(
            id = "hls-apple",
            title = "Apple Bipbop",
            subtitle = "CMAF HLS · multi-bitrate",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            protocol = StreamProtocol.HLS,
        ),
        StreamItem(
            id = "hls-sintel",
            title = "Sintel",
            subtitle = "HLS · Blender Foundation",
            url = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            protocol = StreamProtocol.HLS,
        ),
    )

    val dashCinema = listOf(
        StreamItem(
            id = "dash-bbb",
            title = "Big Buck Bunny",
            subtitle = "DASH · 30 fps",
            url = "https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd",
            protocol = StreamProtocol.DASH,
        ),
        StreamItem(
            id = "dash-sony",
            title = "Sony Test Case",
            subtitle = "DASH264 · SD",
            url = "https://dash.akamaized.net/dash264/TestCases/1a/sony/SNE_DASH_SD_CASE1A_REVISED.mpd",
            protocol = StreamProtocol.DASH,
        ),
    )

    val rows = listOf(
        CatalogRow("Featured HLS", featuredHls),
        CatalogRow("DASH cinema", dashCinema),
    )

    fun find(id: String): StreamItem? = rows.flatMap { it.items }.find { it.id == id }
}
