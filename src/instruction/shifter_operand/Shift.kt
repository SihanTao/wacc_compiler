package instruction.shifter_operand

enum class Shift {
    ASR, LSL, LSR, ROR;

    override fun toString(): String {
        return name
    }
}