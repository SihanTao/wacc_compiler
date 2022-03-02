package backend.instructions

import backend.Cond

class BL(private val cond: Cond, val label: String) : Instruction {
    // BL {cond} Label

    constructor(label: String): this(Cond.NONE, label)
    override fun toString(): String {
        return "BL${cond} $label"
    }
}
