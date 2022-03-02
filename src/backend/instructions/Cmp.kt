package backend.instructions

import backend.ARMRegister
import backend.instructions.operand.Operand2

class Cmp(val Rd: ARMRegister, val operand2: Operand2) : Instruction {
    constructor(Rd: ARMRegister, operand2: Int): this(Rd, Operand2(operand2))

    override fun toString(): String {
        return "CMP $Rd, $operand2"
    }
}
