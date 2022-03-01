package backend.instructions

import backend.ARMRegister
import backend.instructions.addressing.Addressing

class STR(private val register: ARMRegister, private val addressing: Addressing) : Instruction {
    override fun toString(): String {
        return "STR $register, $addressing"
    }
}
