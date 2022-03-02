package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.Register

class ThrowOverflowError(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>


    init {
        val msgCode = codeGenerator.addDataElement("OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\\\n\\\\0")
        instructions = listOf(
                Label("p_throw_overflow_error"),
                Load(Register.R0, StaticRef("msg_$msgCode")),
                Branch(Branch.Mode.LINK, "p_throw_overflow_error"),
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