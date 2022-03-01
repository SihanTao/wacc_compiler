package instruction.shiftOperand

class Immediate(val const: Int): ShifterOperand, ShiftValue {
    override fun toString(): String {
        return "#$const"
    }

}