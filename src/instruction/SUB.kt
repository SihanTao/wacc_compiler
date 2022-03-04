package instruction

import instruction.shifter_operand.*
import register.Register

class SUB(val rd: register.Register, val rn: register.Register,
          val Op: ShifterOperand,
          var cond: Cond = Cond.AL,
          val S: Boolean = false,
):ARM11Instruction {
    constructor(rd: Register, rn: Register, rm: Register, cond: Cond = Cond.AL, S: Boolean = false):
            this(rd=rd, rn=rn, Op= Reg(rm), cond = cond, S = S)

    constructor(rd: Register, rn: Register, Imm: Int, cond: Cond = Cond.AL, S: Boolean = false):
            this(rd=rd, rn=rn, Op=Imm(Imm), cond = cond, S = S)

    constructor(rd: Register, rn: Register, rm: Register, shift: Shift, imm: Int, cond: Cond = Cond.AL, S: Boolean = false):
            this(rd=rd, rn=rn, Op= ShiftImm(rm, shift, imm), cond = cond, S = S)

    constructor(rd: Register, rn: Register, rm: Register, shift: Shift, rs: Register, cond: Cond = Cond.AL, S: Boolean = false):
            this(rd=rd, rn=rn, Op= ShiftReg(rm, shift, rs), cond = cond, S = S)

    fun on(cond: Cond): SUB {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "SUB$cond${s} $rd, $rn, $Op"
    }
}