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

    /** Plural resources by name, to the text of their items, so a plurals entry cannot be missing in one language. */
    private fun loadPlurals(path: String): Map<String, List<String>> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(resDir, path))
        val nodes = doc.getElementsByTagName("plurals")
        return (0 until nodes.length).associate { index ->
            val node = nodes.item(index)
            val items = node.childNodes
            node.attributes.getNamedItem("name").nodeValue to
                (0 until items.length).map { items.item(it) }.filter { it.nodeName == "item" }.map { it.textContent }
        }
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

    @Test
    fun pluralsKeySetsAndPlaceholdersMatch() {
        val en = loadPlurals("values/strings.xml")
        val id = loadPlurals("values-in/strings.xml")
        val bad =
            en.keys.intersect(id.keys).filter { key ->
                en.getValue(key).flatMap(::placeholders).toSet() != id.getValue(key).flatMap(::placeholders).toSet()
            }
        assertTrue(
            "Plurals missing in values-in: ${en.keys - id.keys}; not in values: ${id.keys - en.keys}; placeholder mismatch: $bad",
            en.keys == id.keys && bad.isEmpty(),
        )
    }

    private fun placeholders(text: String) =
        placeholder
            .findAll(text)
            .map { it.value }
            .toList()
            .sorted()
}
