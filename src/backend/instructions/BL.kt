package backend.instructions

import backend.Cond

class BL(private val cond: Cond, val label: String) : Instruction {
    // BL {cond} Label
    override fun toString(): String {
        return "BL $label"
    }
}
