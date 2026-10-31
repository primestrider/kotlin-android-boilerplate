package com.primestrider.kotlinstarter.feature.example

/**
 * The Home list after the search box and the section chips: [selected] is the index of the chosen section
 * (null or out of range = all), and [query] matches an entry's title or subtitle, or its section's title (which
 * keeps the whole section). [text] resolves string resources, so matching follows the app language.
 * Sections left without entries are dropped.
 */
internal fun filterSections(
    sections: List<ExampleSection>,
    query: String,
    selected: Int?,
    text: (Int) -> String,
): List<ExampleSection> {
    val scoped = sections.getOrNull(selected ?: -1)?.let(::listOf) ?: sections
    val needle = query.trim()
    if (needle.isEmpty()) return scoped

    fun matches(res: Int) = text(res).contains(needle, ignoreCase = true)
    return scoped.mapNotNull { section ->
        if (matches(section.title)) {
            section
        } else {
            section.entries
                .filter { matches(it.title) || matches(it.subtitle) }
                .takeIf { it.isNotEmpty() }
                ?.let { section.copy(entries = it) }
        }
    }
}
