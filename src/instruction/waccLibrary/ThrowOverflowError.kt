package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class ThrowOverflowError(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>


    init {
        val msgCode = codeGenerator.addDataElement("OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\\\n\\\\0")
        instructions = listOf(
                LABEL("p_throw_overflow_error"),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                B(B.Mode.LINK, "p_throw_overflow_error"),
        )
        dependencies = listOf(ThrowOverflowError(codeGenerator))
    }


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}