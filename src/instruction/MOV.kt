package instruction

import instruction.shifter_operand.*
import register.Register

class MOV(val rd: register.Register,
          val op: ShifterOperand,
          var cond: Cond = Cond.AL,
          val S: Boolean = false,): ARM11Instruction {

    constructor(rd: register.Register, imm: Int):
            this(rd=rd, op=Imm(imm))

    constructor(rd: register.Register, rn: register.Register):
            this(rd=rd, op=Reg(rn))

    constructor(rd: Register, rm: Register, shift: Shift, imm: Int):
            this(rd=rd, op=ShiftImm(rm, shift, imm))

    constructor(rd: Register, rm: Register, shift: Shift, rs: Register):
            this(rd=rd, op=ShiftReg(rm, shift, rs))

    fun on(cond: Cond): MOV {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "MOV$cond$s $rd, $op"
    }

}