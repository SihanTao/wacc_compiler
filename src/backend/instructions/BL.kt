package backend.instructions

class BL(val label: String) : Instruction {
    // BL {cond} Label
    override fun toString(): String {
        return "BL $label"
    }
}
