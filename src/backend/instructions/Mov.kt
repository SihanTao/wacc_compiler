package backend.instructions

import backend.ARMRegister
import backend.instructions.operand.Operand2

class Mov(Rd: ARMRegister, operand2: Operand2) : Instruction {
    /*
    * MOV{S}{cond} Rd, Operand2
    * MOV{cond} Rd, #imm16
    * */

    private val Rd: ARMRegister? = null
    private val operand2: Operand2? = null

    override fun toString(): String {
        return "MOV $Rd, $operand2"
    }
}
