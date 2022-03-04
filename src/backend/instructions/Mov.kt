package backend.instructions

import backend.register.ARMRegister
import backend.utils.Cond
import backend.utils.Operand2

class Mov(val cond: Cond, val Rd: ARMRegister, val operand2: Operand2) :
    Instruction {
    /*
    * MOV{S}{cond} Rd, Operand2
    * MOV{cond} Rd, #imm16
    * */

    constructor(Rd: ARMRegister, Rm: ARMRegister): this(Cond.NONE, Rd, Operand2(Rm))

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