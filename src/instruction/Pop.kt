package instruction.waccLibrary

import instruction.ARM11Instruction
import register.Register

class Pop(val Register: Register): ARM11Instruction {
    override fun toString(): String {
        return "PUSH {$Register}"
    }
}