package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.ShifterOperand

class And(val rd: register.Register, val rn: register.Register,
          val Op: ShifterOperand,
          val cond: Cond = Cond.AL,
          val S: Boolean = false
):ARM11Instruction {

    constructor(rd: register.Register, rn: register.Register, rm: register.Register):
            this(rd=rd, rn=rn, Op= Register(rm))

    constructor(rd: register.Register, rn: register.Register, Imm: Int):
            this(rd=rd, rn=rn, Op= Immediate(Imm))

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "AND$cond${s} $rd, $rn, $Op"
    }


}