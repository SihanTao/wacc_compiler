package instruction.shifter_operand

import register.Register

class RRX(val Rm: Register): ShifterOperand {
    override fun toString(): String {
        return "$Rm, RRX"
    }
}