package backend.instructions

import backend.ARMRegister

class Pop(register: ARMRegister) : Instruction {
    private val register: ARMRegister

    init {
        this.register = register
    }

    @Override
    override fun toString(): String {
        return "POP {$register}"
    }
}