package backend

import java.lang.IllegalArgumentException

class ARMRegisterAllocator() {
    companion object {
        // R0-R12 is the general purpose register. start from R4 to be the same
        // in refCompile
        val GENERAL_REG_START = 4
        val GENERAL_REG_END = 12
        var counter = GENERAL_REG_START
        fun allocate(): ARMRegister {
            if (counter < GENERAL_REG_START || counter > GENERAL_REG_END) {
                throw IllegalArgumentException("Can't allocate register: R$counter")
            }
            return registers[counter++]
        }

        fun free(): ARMRegister {
            return registers[counter--]
        }

        fun curr(): ARMRegister {
            return registers[counter]
        }

        private val registers: List<ARMRegister> = listOf(
            ARMRegister.R0,
            ARMRegister.R1,
            ARMRegister.R2,
            ARMRegister.R3,
            ARMRegister.R4,
            ARMRegister.R5,
            ARMRegister.R6,
            ARMRegister.R7,
            ARMRegister.R8,
            ARMRegister.R9,
            ARMRegister.R10,
            ARMRegister.R11,
            ARMRegister.R12,
            ARMRegister.SP,
            ARMRegister.LR,
            ARMRegister.PC
        )
    }


}