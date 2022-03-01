package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.Register
import instruction.shiftOperand.ShifterOperand
import register.ARM11Register

class Compare(val Rn: ARM11Register,
              val Op: ShifterOperand,
              val cond: Cond = Cond.AL,
): ARM11Instruction {
    constructor(Rn: ARM11Register, Rm: ARM11Register):
            this(Rn=Rn, Op=Register(Rm))

    constructor(Rn: ARM11Register, Imm: Int):
            this( Rn=Rn, Op=Immediate(Imm))

    override fun toString(): String {
        return "CMP$cond $Rn, $Op"
    }
}