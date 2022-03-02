package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.Register

class ReadChar(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement(" %c\\0")
        instructions = listOf(
                Label("p_read_char"),
                Push(Register.LR),
                Move(Register.R1, Register.R0),
                Load(Register.R0, StaticRef("msg_$msgCode")),
                Add(Register.R0, Register.R0, 4),
                Branch(Branch.Mode.LINK, "scanf"),
                Pop(Register.PC)
        )
        dependencies = listOf()
    }

    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}