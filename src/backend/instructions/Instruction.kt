package backend.instructions

interface Instruction {
    // The default indentation level is set to 2
    // because only labels has indent level 1
    fun indentLevel(): Int {
        return 2
    }
}