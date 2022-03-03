package backend.instructions.operand

import backend.ARMRegister

/* operand specified as Table 1-14 in ARM spec */
class Operand2 {
    enum class Operand2Operator {
        LSL, LSR, ASR, ROR, RRX, NONE
    }

    private var immediate: Immediate?
    private var operator: Operand2Operator
    private var rm: ARMRegister?

    constructor(Rm: ARMRegister?, operator: Operand2Operator, immediate: Immediate?) {
        this.immediate = immediate
        this.operator = operator
        this.rm = Rm
    }

    constructor(immediate: Immediate?) : this(null, Operand2Operator.NONE, immediate)
    constructor(intVal: Int) : this(null, Operand2Operator.NONE, Immediate(intVal))
    constructor(Rm: ARMRegister) : this(Rm, Operand2Operator.NONE, null)
    constructor(Rm: ARMRegister, operator: Operand2Operator) : this(Rm, operator, null)
    constructor(Rm: ARMRegister, operator: Operand2Operator, value: Int): this(Rm, operator, Immediate(value))

    override fun toString(): String {
        val res = StringBuilder()
        if (rm != null) res.append(rm.toString())
        if (operator != Operand2Operator.NONE) res.append(", $operator ")
        if (immediate != null) res.append(immediate.toString())
        return res.toString()
    }
}