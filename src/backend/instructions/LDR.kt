package backend.instructions

import backend.ARMRegister
import backend.instructions.addressing.Addressing
import backend.instructions.addressing.AddressingMode2

class LDR(val register: ARMRegister, val addressing: Addressing, private val mode: LdrMode) : Instruction {

    constructor(register: ARMRegister, addressing: Addressing) : this(register, addressing, LdrMode.LDR)

    constructor(register: ARMRegister, register2: ARMRegister): this(register, AddressingMode2(AddressingMode2.AddrMode2.OFFSET, register2))

    @Override
    override fun toString(): String {
        val str = StringBuilder()
        str.append("$register, ")
        str.append(addressing)
        return "${mode.name} $register, $addressing"
    }

    enum class LdrMode {
        LDR, LDRB, LDRSB, LDREQ, LDRNE, LDRLT, LDRCS
    }
}