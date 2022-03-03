package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.utils.Cond
import backend.instructions.Instruction
import backend.utils.Operand2

class Add : Instruction {
    /* ADD{cond}{S} <Rd>, <Rn>, <operand2> */
    private val cond: Cond
    private val rn: ARMRegister
    private val rd: ARMRegister
    private val operand2: Operand2

    constructor(
        Rd: ARMRegister, Rn: ARMRegister,
        operand2: Operand2
    ) {
        this.rd = Rd
        this.rn = Rn
        this.operand2 = operand2
        this.cond = Cond.NONE
    }

    constructor(
        Rd: ARMRegister, Rn: ARMRegister,
        operand2: Operand2, cond: Cond
    ) {
        this.rd = Rd
        this.rn = Rn
        this.operand2 = operand2
        this.cond = cond
    }

    override fun toString(): String {
        return "ADD$cond $rd, $rn, $operand2"
    }
}