package instruction.shifter_operand

import register.Register

class ShiftImm(val Rm: Register, val shift: Shift, val imm: Int): ShifterOperand {
    override fun toString(): String {
        return "$Rm, $shift #$imm"
    }
}