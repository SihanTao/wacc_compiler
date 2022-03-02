package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.Sign
import instruction.addrMode2.StaticRef
import register.ARM11Register

class ThrowRuntimeError(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        instructions = listOf(
                Label("p_throw_runtime_error"),
                Branch(Branch.Mode.LINK, "p_print_string"),
                Move(ARM11Register.R0, -1),
                Branch(Branch.Mode.LINK, "exit")
        )
        dependencies = listOf(PrintString(codeGenerator))
    }

    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}