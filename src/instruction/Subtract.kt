package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import instruction.shiftOperand.ShifterOperand
import register.ARM11Register

class Subtract(val Rd: ARM11Register, val Rn: ARM11Register,
                val Op: ShifterOperand,
               val cond: Cond = Cond.AL,
               val S: Boolean = false,
):ARM11Instruction {

    constructor(Rd: ARM11Register, Rn: ARM11Register, Rm: ARM11Register):
            this(Rd=Rd, Rn=Rn, Op= Register(Rm))

    constructor(Rd: ARM11Register, Rn: ARM11Register, Imm: Int):
            this(Rd=Rd, Rn=Rn, Op= Immediate(Imm))

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "SUB$cond${s} $Rd, $Rn, $Op"
    }
}