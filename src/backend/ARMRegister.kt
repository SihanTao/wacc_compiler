package backend

import java.util.*

enum class ARMRegister {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10,R11, R12,
    SP, // Stack pointer R13
    LR, // Link register R14
    PC; // Program Counter R15

    override fun toString(): String {
        return name.lowercase(Locale.getDefault())
    }
}