package instruction.shiftOperand

import register.Register

class Register(val Register: Register): ShifterOperand, ShiftValue {
    override fun toString(): String {
        return Register.toString()
    }
}