package backend.instructions.unopAndBiop

import backend.ARMRegister
import backend.instructions.Instruction
import backend.instructions.operand.Operand2

class EOR(
    private val Rd: ARMRegister,
    private val Rn: ARMRegister,
    private val operand2: Operand2
): Instruction {
    override fun toString(): String {
        return "EOR $Rd, $Rn, $operand2"
    }
}