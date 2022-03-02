package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.Register

class Println(codeGenerator: WACCCodeGenerator): WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("\\0")
        instructions = listOf(
                Label("p_print_ln"),
                Push(Register.LR),
                Load(Register.R0, StaticRef("msg_$msgCode")),
                Add(Register.R0, Register.R0, 4),
                Branch(Branch.Mode.LINK, "puts"),
                Move(Register.R0, 0),
                Branch(Branch.Mode.LINK, "fflush"),
                Pop(Register.PC),
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