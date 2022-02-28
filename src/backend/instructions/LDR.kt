package backend.instructions

import backend.ARMRegister
import backend.instructions.addressing.Addressing

class LDR(val register: ARMRegister, val addressing: Addressing, val mode: LdrMode) : Instruction {

    constructor(register: ARMRegister, addressing: Addressing) : this(register, addressing, LdrMode.LDR)

    @Override
    override fun toString(): String {
        val str = StringBuilder()
        str.append("$register, ")
        str.append(addressing)
        return "${mode.name}  $register, $addressing"
    }

    enum class LdrMode {
        LDR, LDRB, LDRSB, LDREQ, LDRNE, LDRLT, LDRCS
    }
}