package backend.instructions.addressing

import backend.instructions.Label

class LabelAddressing(val label: Label) : Addressing {
    // Used for : LDR r4, =msg_0

    override fun toString(): String {
        return "=${label.label}"
    }
}
