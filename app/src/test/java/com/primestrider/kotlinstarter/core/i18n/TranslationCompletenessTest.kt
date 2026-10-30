package com.primestrider.kotlinstarter.core.i18n

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class TranslationCompletenessTest {
    // Gradle runs unit tests with the module directory (app/) as working dir.
    private val resDir = File("src/main/res")
    private val placeholder = Regex("""%(\d+\$)?[sdf]""")

    private fun load(path: String): Map<String, String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(resDir, path))
        val nodes = doc.getElementsByTagName("string")
        return (0 until nodes.length)
            .map { nodes.item(it) }
            .filter { it.attributes.getNamedItem("translatable")?.nodeValue != "false" }
            .associate { it.attributes.getNamedItem("name").nodeValue to it.textContent }
    }

    private val en get() = load("values/strings.xml")
    private val id get() = load("values-in/strings.xml")

    @Test
    fun keySetsAreEqual() {
        val en = en
        val id = id
        val missingInId = en.keys - id.keys
        val extraInId = id.keys - en.keys
        assertTrue(
            "Missing in values-in: $missingInId; not in values: $extraInId",
            missingInId.isEmpty() && extraInId.isEmpty(),
        )
    }

    @Test
    fun placeholdersMatch() {
        val en = en
        val id = id
        val bad =
            en.keys.intersect(id.keys).filter { key ->
                placeholders(en.getValue(key)) != placeholders(id.getValue(key))
            }
        assertTrue("Placeholder mismatch for keys: $bad", bad.isEmpty())
    }

    private fun placeholders(text: String) =
        placeholder
            .findAll(text)
            .map { it.value }
            .toList()
            .sorted()
}
