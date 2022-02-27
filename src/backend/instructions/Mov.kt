package backend.instructions

import backend.ARMRegister
import backend.instructions.operand.Operand2

class Mov(val Rd: ARMRegister, val operand2: Operand2) : Instruction {
    /*
    * MOV{S}{cond} Rd, Operand2
    * MOV{cond} Rd, #imm16
    * */

    override fun toString(): String {
        return "MOV $Rd, $operand2"
    }
}
