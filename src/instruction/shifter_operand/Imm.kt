package instruction.shifter_operand

class Imm(val imm: Int): ShifterOperand{
    override fun toString(): String {
        return "#$imm"
    }
}