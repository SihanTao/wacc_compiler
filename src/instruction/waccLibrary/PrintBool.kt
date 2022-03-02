package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.Register

class PrintBool(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msg1 = codeGenerator.addDataElement("true\\0")
        val msg2 = codeGenerator.addDataElement("false\\0")
        instructions = listOf(
                Label("p_print_bool"),
                Push(Register.LR),
                Compare(Register.R0, 0),
                Load(Register.R0, StaticRef("msg_$msg1"), cond=Cond.NE),
                Load(Register.R0, StaticRef("msg_$msg2"), cond=Cond.NE),
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