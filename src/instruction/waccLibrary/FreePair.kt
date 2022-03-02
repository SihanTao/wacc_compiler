package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.Sign
import instruction.addressing_mode.StaticRef
import register.Register

class FreePair : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(ThrowRuntimeError())

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        return listOf(
                LABEL("p_free_pair"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode")).on(Cond.EQ),
                B("p_throw_runtime_error", cond=Cond.EQ),
                PUSH(Register.R0),
                LDR(Register.R0, ImmOffset(Register.R0)),
                BL("free"),
                LDR(Register.R0, ImmOffset(Register.SP)),
                LDR(Register.R0, ImmOffset(Register.R0, Pair(Sign.PLUS, 4))),
                BL("free"),
                POP(Register.R0),
                BL("free"),
                POP(Register.PC)
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}