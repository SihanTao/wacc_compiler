package register

import java.util.*

class ARM11RegisterAllocator {
    private val availableRegisters =
            mutableListOf(ARM11Register.R4, ARM11Register.R5, ARM11Register.R6,
                    ARM11Register.R7,ARM11Register.R8,ARM11Register.R9,
                    ARM11Register.R10,ARM11Register.R11,ARM11Register.R12
            )

    fun peekRegister() : ARM11Register{
        return availableRegisters.first()
    }

    fun consumeRegister(): ARM11Register {
        return availableRegisters.removeFirst()
    }

    fun freeRegister(register: ARM11Register) {
        availableRegisters.add(0, register)
    }


}