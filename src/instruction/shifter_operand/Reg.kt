package instruction.shifter_operand

import register.Register

class Reg(val Rm: Register): ShifterOperand {
    override fun toString(): String {
        return Rm.toString()
    }
}