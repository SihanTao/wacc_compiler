package instruction

import instruction.shifter_operand.*
import register.Register

class CMP(val rn: register.Register,
          val Op: ShifterOperand,
          var cond: Cond = Cond.AL,
): ARM11Instruction {
    constructor(rn: Register, rm: Register):
            this(rn=rn, Op=Reg(rm))

    constructor(rn: Register, imm: Int):
            this(rn=rn, Op=Imm(imm))

    constructor(rn: Register, rm: Register, shift: Shift, imm: Int):
            this(rn=rn, Op=ShiftImm(rm, shift, imm))

    constructor(rn: Register, rm: Register, shift: Shift, rs: Register):
            this(rn=rn, Op=ShiftReg(rm, shift, rs))

    fun on(cond: Cond): CMP {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "CMP$cond $rn, $Op"
    }
}