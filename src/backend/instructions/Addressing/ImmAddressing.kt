package backend.instructions.Addressing;

public class ImmAddressing implements Addressing {
    private int imm;

    public ImmAddressing(int value) {
        this.imm = value;
    }

    @Override
    public String toString() {
        return "=" + imm;
    }
}
