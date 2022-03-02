package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import register.Register

class ThrowRuntimeError : WACCLibraryFunction() {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        instructions = listOf(
                LABEL("p_throw_runtime_error"),
                B(B.Mode.LINK, "p_print_string"),
                MOV(Register.R0, -1),
                B(B.Mode.LINK, "exit")
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