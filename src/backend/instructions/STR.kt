package backend.instructions

import backend.ARMRegister
import backend.instructions.addressing.Addressing

class STR(
    private val register: ARMRegister,
    private val addressing: Addressing,
    private val mode: STRMode
) : Instruction {

    constructor(register: ARMRegister, addressing: Addressing) : this(
        register,
        addressing,
        STRMode.STR
    )

    override fun toString(): String {
        return "$mode $register, $addressing"
    }

    enum class STRMode { STR, STRB; }
}
