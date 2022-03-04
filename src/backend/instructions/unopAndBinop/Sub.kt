package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.utils.Cond
import backend.instructions.Instruction
import backend.utils.Operand2

class Sub(
    private val Rd: ARMRegister,
    private val Rn: ARMRegister,
    private val operand2: Operand2,
    private val cond: Cond
) : Instruction {

    /* SUB{cond}{S} <Rd>, <Rn>, <operand2> */

    constructor(rd: ARMRegister, rn: ARMRegister, constant: Int) : this(
        rd,
        rn,
        Operand2(constant),
        Cond.NONE
    )

    override fun toString(): String {
        return "SUB$cond $Rd, $Rn, $operand2"
    }
}