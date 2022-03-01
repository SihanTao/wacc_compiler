package instruction.shiftOperand

enum class Shift {
    ASR, LSL, LSR, ROR, RRX;

    override fun toString(): String {
        return name
    }
}