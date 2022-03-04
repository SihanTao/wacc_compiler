package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.instructions.Instruction
import backend.utils.Operand2

class OR(
    private val Rd: ARMRegister,
    private val Rn: ARMRegister,
    private val operand2: Operand2
) : Instruction {
    override fun toString(): String {
        return "ORR $Rd, $Rn, $operand2"
    }

}