package instruction

import register.Register

class PUSH(val Register: Register): ARM11Instruction {
    override fun toString(): String {
        return "PUSH {$Register}"
    }
}