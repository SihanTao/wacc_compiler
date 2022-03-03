package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.instructions.Instruction
import backend.utils.Operand2

class SMULL(
    private val Rd: ARMRegister,
    private val operand2: Operand2
): Instruction {
    override fun toString(): String {
        return "SMULL $Rd, $operand2, $Rd, $operand2"
    }
}