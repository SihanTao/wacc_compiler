package backend.instructions;

import backend.ARMRegister;

public class LDR extends Instruction {
    private final ARMRegister register;
    private final Addressing addressing;

    public LDR(ARMRegister register, Addressing addressing) {
        this.register = register;
        this.addressing = addressing;
    }

    @Override
    public String toString() {
        String stringBuilder = register + ", " +
                addressing;
        return "LDR " + stringBuilder;
    }
}
