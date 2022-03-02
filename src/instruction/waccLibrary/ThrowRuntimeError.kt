package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import register.Register

class ThrowRuntimeError : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(PrintString())


    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        return listOf(
                LABEL("p_throw_runtime_error"),
                BL("p_print_string"),
                MOV(Register.R0, -1),
                BL("exit")
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}