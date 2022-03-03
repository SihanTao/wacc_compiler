package backend.instructions

import backend.Cond

class B(val cond: Cond, val label: Label): Instruction {
    constructor(cond: Cond, labelName: String): this(cond, Label(labelName))
    constructor(labelName: String): this(Cond.NONE, Label(labelName))

    override fun toString(): String {
        return "B$cond ${label.label}"
    }
}