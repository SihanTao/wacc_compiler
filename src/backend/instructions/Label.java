package backend.instructions;

public class Label extends Instruction {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }

    public String getLabel() {
        return label;
    }
}
