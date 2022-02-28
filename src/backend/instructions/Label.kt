package backend.instructions

class Label(val label: String) : Instruction, Comparable<Label> {

    @Override
    override fun toString(): String {
        return "$label:"
    }

    // No need to override indentation level here: default is 1
    override fun indentLevel(): Int {
        return 1
    }

    override fun compareTo(other: Label): Int {
        return this.label.compareTo(other.label)
    }

}