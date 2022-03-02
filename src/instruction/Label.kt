package instruction

class Label(val label: String): ARM11Instruction {

    override fun toString(): String {
        return "$label:"
    }
}