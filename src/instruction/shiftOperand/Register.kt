package instruction.shiftOperand

import register.ARM11Register

class Register(val register: ARM11Register): ShifterOperand, ShiftValue {
    override fun toString(): String {
        return register.toString()
    }
}