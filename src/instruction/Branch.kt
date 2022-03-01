package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import register.ARM11Register

class Branch(
        val L: Mode = Mode.NORM,
        val cond: Cond = Cond.AL,
        val addr: String
): ARM11Instruction {

    constructor(L: Mode, str: String): this(L=L, addr=str)

    override fun toString(): String {
        val l = if (L) "L" else ""
        return "B$l$cond $addr"
    }

    enum class Mode{
        NORM, LINK
    }

}