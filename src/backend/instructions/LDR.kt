package backend.instructions

import backend.ARMRegister
import backend.instructions.Addressing.Addressing

class LDR(register: ARMRegister, addressing: Addressing) : Instruction {
    private val register: ARMRegister
    private val addressing: Addressing

    init {
        this.register = register
        this.addressing = addressing
    }

    @Override
    override fun toString(): String {
        val stringBuilder: String = register.toString() + ", " +
                addressing
        return "LDR $stringBuilder"
    }
}