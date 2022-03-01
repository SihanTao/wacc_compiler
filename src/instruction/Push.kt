package instruction

import register.ARM11Register

class Push(val register: ARM11Register): ARM11Instruction {
    override fun toString(): String {
        return "PUSH {$register}"
    }
}