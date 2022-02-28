package backend.instructions

import backend.Cond

class B(val label: Label, val cond: Cond): Instruction {
    constructor(labelName: String, cond: Cond): this(Label(labelName), cond)
    constructor(labelName: String): this(Label(labelName), Cond.NONE)

    override fun toString(): String {
        return "B$cond ${label.label}"
    }
}