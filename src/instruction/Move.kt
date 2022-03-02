package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.ShifterOperand

class Move(val rd: register.Register,
           val shifterOperand: ShifterOperand,
           val cond: Cond = Cond.AL,
           val S: Boolean = false,): ARM11Instruction {

    constructor(rd: register.Register, imm: Int): this(rd=rd, shifterOperand=Immediate(imm))
    constructor(rd: register.Register, rn: register.Register): this(rd=rd, shifterOperand=Register(rn))

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "MOV$cond$s $rd, $shifterOperand"
    }

}