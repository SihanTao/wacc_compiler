package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import register.ARM11Register

class Label(val label: String): ARM11Instruction {

    override fun toString(): String {
        return "$label:"
    }
}