package backend.utils

import backend.register.ARMRegister

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

    /* Can be a constant */
    constructor(immediate: Immediate?) : this(null,
        Operand2Operator.NONE, immediate)
    constructor(intVal: Int) : this(null,
        Operand2Operator.NONE, Immediate(intVal)
    )

    /* Can be a register with optional shift
    *  Rm{, shift}
    * */
    constructor(Rm: ARMRegister) : this(Rm, Operand2Operator.NONE, null)
    constructor(Rm: ARMRegister, operator: Operand2Operator) : this(Rm, operator, null)
    constructor(Rm: ARMRegister, operator: Operand2Operator, value: Int): this(Rm, operator, Immediate(value))

    override fun toString(): String {
        val res = StringBuilder()
        if (rm != null) res.append("$rm")
        if (operator != Operand2Operator.NONE) res.append(", $operator ")
        if (immediate != null) res.append("$immediate")
        return res.toString()
    }
}