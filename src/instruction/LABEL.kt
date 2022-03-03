package instruction

class LABEL(val label: String): ARM11Instruction {

    override fun toString(): String {
        return "$label:"
    }

    companion object {
        private var labelCounter = 0;
        fun nextNo(): LABEL { return LABEL("L${labelCounter++}")}
    }
}