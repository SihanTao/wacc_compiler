package backend.instructions

import backend.register.ARMRegister
import backend.instructions.addressing.Addressing
import backend.instructions.addressing.AddressingMode2

class STR(
    private val register: ARMRegister,
    private val addressing: Addressing,
    private val mode: STRMode
) : Instruction {
    constructor(Rd: ARMRegister, Rm: ARMRegister, mode: STRMode): this(
        Rd, AddressingMode2(Rm), mode
    )

    constructor(Rd: ARMRegister, Rm: ARMRegister): this(
        Rd, AddressingMode2(Rm), STRMode.STR
    )

    constructor(Rd: ARMRegister, Rm: ARMRegister, offset: Int): this(
        Rd, AddressingMode2(Rm, offset), STRMode.STR
    )

    constructor(mode: STRMode, Rd: ARMRegister, Rm: ARMRegister, offset: Int): this(
        Rd, AddressingMode2(Rm, offset), mode
    )

    override fun toString(): String {
        return "$mode $register, $addressing"
    }

    enum class STRMode { STR, STRB; }
}
