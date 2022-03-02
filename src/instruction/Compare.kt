package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.ShifterOperand

class Compare(val rn: register.Register,
              val Op: ShifterOperand,
              val cond: Cond = Cond.AL,
): ARM11Instruction {
    constructor(rn: register.Register, rm: register.Register):
            this(rn=rn, Op=Register(rm))

    constructor(rn: register.Register, Imm: Int):
            this( rn=rn, Op=Immediate(Imm))

    override fun toString(): String {
        return "CMP$cond $rn, $Op"
    }
}