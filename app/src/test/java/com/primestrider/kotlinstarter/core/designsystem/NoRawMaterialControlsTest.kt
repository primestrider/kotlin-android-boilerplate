package com.primestrider.kotlinstarter.core.designsystem

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Form controls come from core/designsystem/component; screens never use the stock Material ones. */
class NoRawMaterialControlsTest {
    private val rawControl =
        Regex(
            """import androidx\.compose\.material3\.(Checkbox|TriStateCheckbox|RadioButton|Switch|Slider|RangeSlider|SegmentedButton|""" +
                """SingleChoiceSegmentedButtonRow|MultiChoiceSegmentedButtonRow|TextField|OutlinedTextField)\b""",
        )

    @Test
    fun feature_code_uses_design_system_controls_only() {
        val root = File("src/main/java")
        assertTrue("run from the module directory: ${root.absolutePath} is missing", root.isDirectory)
        val offenders =
            root
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .filterNot { it.invariantSeparatorsPath.contains("core/designsystem/component/") }
                .filter { rawControl.containsMatchIn(it.readText()) }
                .map { it.invariantSeparatorsPath }
                .toList()
        assertTrue("Use the design-system controls instead of Material ones in:\n${offenders.joinToString("\n")}", offenders.isEmpty())
    }
}
