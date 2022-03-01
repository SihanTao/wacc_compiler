package instruction.waccLibrary

import instruction.ARM11Instruction
import register.ARM11Register

class Pop(val register: ARM11Register): ARM11Instruction {
    override fun toString(): String {
        return "PUSH {$register}"
    }
}