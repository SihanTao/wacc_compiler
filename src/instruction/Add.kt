package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import instruction.shiftOperand.ShifterOperand
import register.ARM11Register

class Add(val cond: Cond = Cond.AL,
          val S: Boolean = false,
          val Rd: ARM11Register, val Rn: ARM11Register,
          val Op: ShifterOperand
):ARM11Instruction {

    constructor(Rd: ARM11Register, Rn: ARM11Register, Rm: ARM11Register):
            this(Rd=Rd, Rn=Rn, Op=Register(Rm))

    constructor(Rd: ARM11Register, Rn: ARM11Register, Imm: Int):
            this(Rd=Rd, Rn=Rn, Op=Immediate(Imm))

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "ADD$cond${s} $Rd, $Rn, $Op"
    }


}