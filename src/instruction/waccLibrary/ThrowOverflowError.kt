package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class ThrowOverflowError : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(ThrowRuntimeError())


    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0")
        return listOf(
                LABEL("p_throw_overflow_error"),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                BL("p_throw_overflow_error")
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}