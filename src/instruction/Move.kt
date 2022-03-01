package instruction

import instruction.shiftOperand.Immediate
import instruction.shiftOperand.ShifterOperand
import register.ARM11Register

class Move(val cond: Cond = Cond.AL,
           val S: Boolean = false,
           val Rd: ARM11Register,
           val shifterOperand: ShifterOperand): ARM11Instruction {

    constructor(Rd: ARM11Register, imm: Int): this(Rd=Rd, shifterOperand=Immediate(imm))

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "MOV$cond$s $Rd, $shifterOperand"
    }

}