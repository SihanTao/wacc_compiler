package backend.instructions;

import backend.ARMRegister;

public class Push extends Instruction {
    private ARMRegister register;

    public Push(ARMRegister register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return "PUSH {" + register.toString() + "}";
    }
}
