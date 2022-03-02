package instruction.shifter_operand

import register.Register

class ShiftReg(val Rm: Register, val shift: Shift, val Rs: Register): ShifterOperand {
    override fun toString(): String {
        return "$Rm, $shift ${Rs}"
    }
}