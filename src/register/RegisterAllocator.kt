package register

class RegisterAllocator {
    private val availableRegisters =
            mutableListOf(Register.R4, Register.R5, Register.R6,
                    Register.R7,Register.R8,Register.R9,
                    Register.R10,Register.R11
            )

    fun peekRegister() : Register{
        return availableRegisters.first()
    }

    fun consumeRegister(): Register {
        return availableRegisters.removeFirst()
    }

    fun freeRegister(Register: Register) {
        availableRegisters.add(0, Register)
    }

    fun noOfFreeRegisters(): Int {
        return availableRegisters.count()
    }


}