package backend.instructions

import backend.ARMRegister
import backend.Cond
import backend.instructions.operand.Operand2

class Mov(val cond: Cond, val Rd: ARMRegister, val operand2: Operand2) :
    Instruction {
    /*
    * MOV{S}{cond} Rd, Operand2
    * MOV{cond} Rd, #imm16
    * */

    constructor(Rd: ARMRegister, operand2: Operand2) : this(
        Cond.NONE,
        Rd,
        operand2
    )

    constructor(cond: Cond, Rd: ARMRegister, operand2: Int) : this(
        cond,
        Rd,
        Operand2(operand2)
    )

    override fun toString(): String {
        return "MOV$cond $Rd, $operand2"
    }
}
