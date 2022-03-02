package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import org.antlr.v4.codegen.CodeGenerator
import register.ARM11Register

class PrintBool(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msg1 = codeGenerator.addDataElement("true\\0")
        val msg2 = codeGenerator.addDataElement("false\\0")
        instructions = listOf(
                Label("p_print_bool"),
                Push(ARM11Register.LR),
                Compare(ARM11Register.R0, 0),
                Load(ARM11Register.R0, StaticRef("msg_$msg1"), cond=Cond.NE),
                Load(ARM11Register.R0, StaticRef("msg_$msg2"), cond=Cond.NE),
                Add(ARM11Register.R0, ARM11Register.R0, 4),
                Branch(Branch.Mode.LINK, "printf"),
                Move(ARM11Register.R0, 0),
                Branch(Branch.Mode.LINK, "fflush"),
                Pop(ARM11Register.PC),
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