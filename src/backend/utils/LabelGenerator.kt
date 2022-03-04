package backend.utils

import backend.instructions.Label

class LabelGenerator(private val header: String) {
    private var cnt: Int = 0

    fun getLabel(): Label {
        return Label(header + cnt++)
    }
}
