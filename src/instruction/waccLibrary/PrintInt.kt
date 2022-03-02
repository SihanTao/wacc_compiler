package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.Register

class PrintInt(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msg = codeGenerator.addDataElement("%d\\0")
        instructions = listOf(
                Label("p_print_int"),
                Push(Register.LR),
                Move(Register.R1, Register.R0),
                Load(Register.R0, StaticRef("msg_$msg")),
                Add(Register.R0, Register.R0, 4),
                Branch(Branch.Mode.LINK, "printf"),
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