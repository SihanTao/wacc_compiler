package instruction

class LABEL(val label: String): ARM11Instruction {

    override fun toString(): String {
        return "$label:"
    }
}