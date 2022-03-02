package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.StaticRef
import register.Register

class CheckArrayBounds : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(ThrowRuntimeError())

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode1 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: negative index\\n\\0")
        val msgCode2 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: index too large\\n\\0")
        return listOf(
                LABEL("p_check_array_bounds"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode1"), cond=Cond.LT),
                BL("p_throw_runtime_error", Cond.LT),
                LDR(Register.R1, ImmOffset(Register.R1)),
                CMP(Register.R0, Register.R1),
                LDR(Register.R0, StaticRef("msg_$msgCode2")).on(Cond.CS),
                BL("p_throw_runtime_error").on(Cond.CS),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}