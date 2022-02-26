package backend

import backend.instructions.Instruction
import java.util.List

class Code(instructions: List<Instruction>) : Directive {
    private val instructions: List<Instruction>

    init {
        this.instructions = instructions
    }

    @Override
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\t.global main")
        for (instruction in instructions) {
            stringBuilder.append(instruction.toString()).append("\n")
        }
        return stringBuilder.toString()
    }
}