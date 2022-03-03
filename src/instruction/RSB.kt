package instruction

import instruction.shifter_operand.*
import register.Register

class RSB(val rd: Register, val rn: Register,
          val Op: ShifterOperand,
          var cond: Cond = Cond.AL,
          var S: Boolean = false,
):ARM11Instruction {
    constructor(rd: Register, rn: Register, rm: Register):
            this(rd=rd, rn=rn, Op= Reg(rm))

    constructor(rd: Register, rn: Register, Imm: Int):
            this(rd=rd, rn=rn, Op= Imm(Imm))

    constructor(rd: Register, rn: Register, rm: Register, shift: Shift, imm: Int):
            this(rd=rd, rn=rn, Op= ShiftImm(rm, shift, imm))

    constructor(rd: Register, rn: Register, rm: Register, shift: Shift, rs: Register):
            this(rd=rd, rn=rn, Op= ShiftReg(rm, shift, rs))

    fun on(cond: Cond): RSB {
        this.cond = cond
        return this
    }

    fun S(): RSB {
        this.S = true
        return this
    }

    override fun toString(): String {
        val s = if (S) "S" else ""
        return "RSB$cond${s} $rd, $rn, $Op"
    }

}