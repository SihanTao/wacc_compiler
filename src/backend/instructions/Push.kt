package backend.instructions

import backend.register.ARMRegister

class Push(register: ARMRegister) : Instruction {
    private val register: ARMRegister

    init {
        this.register = register
    }

    @Override
    override fun toString(): String {
        return "PUSH {$register}"
    }
}