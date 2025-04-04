package com.example

class IptvPlaylistParser {
    data class M3UItem(
        val title: String?,
        val attributes: Map<String, String>,
        val url: String?
    )

    data class M3UPlaylist(
        val items: List<M3UItem>
    )

    fun parseM3U(content: String): M3UPlaylist {
        val lines = content.lines()
        val items = mutableListOf<M3UItem>()
        var currentTitle: String? = null
        var currentAttributes = mutableMapOf<String, String>()

        for (i in lines.indices) {
            val line = lines[i].trim()
            
            when {
                line.startsWith("#EXTINF:") -> {
                    val attributeString = line.substringAfter(",")
                    currentTitle = attributeString.substringAfterLast(",")
                    
                    // Parse attributes
                    val attrRegex = """([a-zA-Z-]+)="([^"]*)\"""".toRegex()
                    attrRegex.findAll(line).forEach { matchResult ->
                        val (key, value) = matchResult.destructured
                        currentAttributes[key] = value
                    }
                }
                !line.startsWith("#") && line.isNotEmpty() -> {
                    items.add(M3UItem(currentTitle, currentAttributes.toMap(), line))
                    currentTitle = null
                    currentAttributes.clear()
                }
            }
        }

        return M3UPlaylist(items)
    }
}