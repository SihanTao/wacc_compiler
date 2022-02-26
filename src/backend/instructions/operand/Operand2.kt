package backend.instructions.operand

import backend.ARMRegister

/* operand specified as Table 1-14 in ARM spec */
class Operand2 {
    enum class Operand2Operator {
        LSL, LSR, ASR, ROR, RRX, NONE
    }

    private var immed: Immediate?
    private var operator: Operand2Operator
    private var Rm: ARMRegister?

    private constructor(Rm: ARMRegister?, operator: Operand2Operator, immed: Immediate?) {
        this.immed = immed
        this.operator = operator
        this.Rm = Rm
    }

    constructor(immed: Immediate?) : this(null, Operand2Operator.NONE, immed) {}
    constructor(intVal: Int) : this(null, Operand2Operator.NONE, Immediate(intVal)) {}
    constructor(Rm: ARMRegister) : this(Rm, Operand2Operator.NONE, null) {}
    constructor(Rm: ARMRegister, operator: Operand2Operator) : this(Rm, operator, null) {}

    override fun toString(): String {
        val res = StringBuilder()
        if (Rm != null) res.append(Rm.toString())
        if (operator != Operand2Operator.NONE) res.append(", $operator ")
        if (immed != null) res.append(immed.toString())
        return res.toString()
    }
}