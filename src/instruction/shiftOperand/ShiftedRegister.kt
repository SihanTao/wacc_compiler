package instruction.shiftOperand

import register.Register

class ShiftedRegister(val Register: Register, val shift: Shift, val shiftValue: ShiftValue) {
    override fun toString(): String {
        return "$Register, $shift ${shiftValue}t"
    }
}