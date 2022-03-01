package instruction.shiftOperand

import register.ARM11Register

class ShiftedRegister(val register: ARM11Register, val shift: Shift, val shiftValue: ShiftValue) {
    override fun toString(): String {
        return "$register, $shift ${shiftValue}t"
    }
}