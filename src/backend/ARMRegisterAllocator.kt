package backend

class ARMRegisterAllocator {
    companion object {
        // R0-R12 is the general purpose register. start from R4 to be the same
        // in refCompile
        private const val GENERAL_REG_START = 4
        private const val GENERAL_REG_END = 12
        private const val MAX_ARM_REGISTER = 16
        private var counter = GENERAL_REG_START

        fun allocate(): ARMRegister {
            if (counter < GENERAL_REG_START || counter > GENERAL_REG_END) {
                throw IllegalArgumentException("Can't allocate register: R$counter")
            }
            return registers[counter++]
        }

        fun free(): ARMRegister {
            return registers[--counter]
        }

        // The counter is the next free reg to be used
        fun curr(): ARMRegister {
            return registers[if (counter > GENERAL_REG_START) counter - 1 else counter]
        }

        fun last(): ARMRegister {
            return registers[if (counter > GENERAL_REG_START) counter - 2 else counter]
        }

        fun next(): ARMRegister? {
            return if (counter > MAX_ARM_REGISTER) null else registers[counter]
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