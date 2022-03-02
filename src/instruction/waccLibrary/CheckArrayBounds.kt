package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.StaticRef
import register.Register

class CheckArrayBounds(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode1 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: negative index\\n\\0")
        val msgCode2 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: index too large\\n\\0")
        instructions = listOf(
                LABEL("p_check_array_bounds"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode1"), cond= Cond.LT),
                B(B.Mode.LINK, "p_throw_runtime_error", Cond.LT),
                LDR(Register.R1, ImmOffset(Register.R1)),
                LDR(Register.R0, StaticRef("msg_$msgCode2"), cond= Cond.CS),
                B(B.Mode.LINK, "p_throw_runtime_error", Cond.CS),
                POP(Register.PC),
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