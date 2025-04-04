package com.keyiflerolsun

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.example.IptvPlaylistParser

class CanliTV : MainAPI() {
    override var mainUrl              = "https://raw.githubusercontent.com/AlphaTurksss/canlitv/main/channels.m3u"
    override var name                 = "Canlı TV"
    override var lang                 = "tr"
    override val hasMainPage          = true
    override val hasChromecastSupport = true
    override val supportedTypes       = setOf(TvType.Live)

    override suspend fun getMainPage(page: Int, request: MainPageRequest, filters: List<String>?): HomePageResponse {
        val parser = IptvPlaylistParser()
        val response = app.get(mainUrl).text
        val playlist = parser.parseM3U(response)

        val channels = playlist.items.map { channel ->
            LiveSearchResponse(
                name = channel.title ?: "",
                url = channel.url ?: "",
                apiName = this.name,
                type = TvType.Live,
                posterUrl = channel.attributes["tvg-logo"] ?: ""
            )
        }

        return HomePageResponse(
            listOf(HomePageList("Kanallar", channels))
        )
    }

    override suspend fun load(url: String): LoadResponse {
        return LiveStreamLoadResponse(
            name = url,
            url = url,
            apiName = this.name,
            dataUrl = url
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(
            ExtractorLink(
                source = this.name,
                name = this.name,
                url = data,
                referer = "",
                quality = Qualities.Unknown.value,
                isM3u8 = true
            )
        )
        return true
    }
}