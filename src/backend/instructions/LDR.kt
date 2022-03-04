package backend.instructions

import backend.register.ARMRegister
import backend.instructions.addressing.Addressing
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.ImmAddressing
import backend.instructions.addressing.LabelAddressing

class LDR(private val register: ARMRegister, private val addressing: Addressing, private val mode: LdrMode) : Instruction {

    constructor(register: ARMRegister, addressing: Addressing) : this(register, addressing, LdrMode.LDR)

    // Constructor for immediate : e.g. LDR r4, =4
    constructor(register: ARMRegister, int: Int): this(register, ImmAddressing(int))

    // Constructor for labels : LDR r4, =msg_0
    constructor(register: ARMRegister, label: Label): this(register, LabelAddressing(label))
    constructor(register: ARMRegister, register2: ARMRegister): this(register, AddressingMode2(register2))

    @Override
    override fun toString(): String {
        val str = StringBuilder()
        str.append("$register, ")
        str.append(addressing)
        return "${mode.name} $register, $addressing"
    }

    enum class LdrMode {
        LDR, LDRSB, LDREQ, LDRNE, LDRLT, LDRCS
    }
}