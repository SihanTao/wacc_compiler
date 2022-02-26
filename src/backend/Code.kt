package backend

import backend.instructions.Instruction

class Code(instructions: MutableList<Instruction>) : Directive {
    private val instructions: MutableList<Instruction>

    init {
        this.instructions = instructions
    }

    @Override
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\t.global main\n")
        for (instruction in instructions) {
            stringBuilder.append(instruction.toString()).append("\n")
        }
        return stringBuilder.toString()
    }
}