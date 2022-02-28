package backend.instructionGenerator

import backend.instructions.Label

class LabelGenerator(private val header: String) {
    var cnt: Int = 0

    fun getLabel(): Label {
        return Label(header + cnt++)
    }
}
