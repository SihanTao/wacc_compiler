package backend.instructions.unopAndBinop

import backend.ARMRegister
import backend.Cond
import backend.instructions.Instruction
import backend.instructions.operand.Operand2

class Add : Instruction {
    /* ADD{cond}{S} <Rd>, <Rn>, <operand2> */
    private val cond: Cond
    private val Rn: ARMRegister
    private val Rd: ARMRegister
    private val operand2: Operand2

    constructor(
        Rd: ARMRegister, Rn: ARMRegister,
        operand2: Operand2
    ) {
        this.Rd = Rd
        this.Rn = Rn
        this.operand2 = operand2
        this.cond = Cond.NONE
    }

    constructor(
        Rd: ARMRegister, Rn: ARMRegister,
        operand2: Operand2, cond: Cond
    ) {
        this.Rd = Rd
        this.Rn = Rn
        this.operand2 = operand2
        this.cond = cond
    }

    override fun toString(): String {
        return "ADD$cond $Rd, $Rn, $operand2"
    }
}