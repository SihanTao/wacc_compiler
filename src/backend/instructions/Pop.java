package backend.instructions;

import backend.ARMRegister;

public class Pop extends Instruction {
    private ARMRegister register;

    public Pop(ARMRegister register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return "POP {" + register.toString() + "}";
    }
}
