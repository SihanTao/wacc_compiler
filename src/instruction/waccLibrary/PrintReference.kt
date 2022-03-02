package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.StaticRef
import register.ARM11Register

class PrintReference(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        instructions = listOf(
                Label("p_check_null_pointer"),
                Push(ARM11Register.LR),
                Compare(ARM11Register.R0, 0),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode"), cond=Cond.EQ),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.EQ),
                Pop(ARM11Register.PC),
        )
        dependencies = listOf(ThrowRuntimeError(codeGenerator))
    }


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}