package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import register.ARM11Register

class Branch(
        val L: Boolean = false,
        val cond: Cond = Cond.AL,
        val addr: String
): ARM11Instruction {

    override fun toString(): String {
        val l = if (L) "L" else ""
        return "B$l$cond $addr"
    }

}