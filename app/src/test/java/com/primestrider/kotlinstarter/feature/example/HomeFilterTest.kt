package com.primestrider.kotlinstarter.feature.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeFilterTest {
    private val text =
        mapOf(
            1 to "Features",
            2 to "Real screens",
            3 to "Products",
            4 to "Paging and search",
            5 to "Todos",
            6 to "Room on device",
            7 to "Device APIs",
            8 to "Android screens",
            9 to "Camera & QR Scanner",
            10 to "CameraX preview",
        )

    private val features = ExampleSection(1, 2, listOf(ExampleEntry(3, 4, "products"), ExampleEntry(5, 6, "todos")), Icons.Filled.Star)
    private val device = ExampleSection(7, 8, listOf(ExampleEntry(9, 10, "camera")), Icons.Filled.Star)
    private val sections = listOf(features, device)

    private fun filter(
        query: String,
        selected: Int? = null,
    ) = filterSections(sections, query, selected) { text.getValue(it) }

    @Test
    fun blank_query_and_no_section_keeps_everything() {
        assertEquals(sections, filter("  "))
    }

    @Test
    fun query_matches_titles_and_subtitles_ignoring_case_and_drops_empty_sections() {
        assertEquals(listOf(device), filter("CAMERAX"))
        assertEquals(listOf(features.copy(entries = listOf(features.entries[1]))), filter("room"))
    }

    @Test
    fun query_matching_a_section_title_keeps_its_whole_section() {
        assertEquals(listOf(device), filter("device apis"))
    }

    @Test
    fun selected_section_limits_the_list_and_combines_with_the_query() {
        assertEquals(listOf(features), filter("", selected = 0))
        assertEquals(emptyList<ExampleSection>(), filter("camera", selected = 0))
    }

    @Test
    fun out_of_range_selection_falls_back_to_all_sections() {
        assertEquals(sections, filter("", selected = 5))
    }
}
