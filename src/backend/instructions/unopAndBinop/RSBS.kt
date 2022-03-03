package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.instructions.Instruction
import backend.utils.Operand2

class RSBS(
    private val Rd: ARMRegister,
    private val Rn: ARMRegister,
    private val operand2: Operand2
) : Instruction {

    constructor(Rd: ARMRegister, Rn: ARMRegister, value: Int) : this(
        Rd,
        Rn,
        Operand2(value)
    )

    override fun toString(): String {
        return "RSBS $Rd, $Rn, $operand2"
    }
}