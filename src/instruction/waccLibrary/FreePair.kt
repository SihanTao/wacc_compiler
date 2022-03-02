package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.Sign
import instruction.addressing_mode.StaticRef
import register.Register

class FreePair(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        instructions = listOf(
                LABEL("p_free_pair"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                B("p_throw_runtime_error", cond=Cond.EQ),
                LDR(Register.R0, ImmOffset(Register.R0)),
                B(B.Mode.LINK, "free"),
                LDR(Register.R0, ImmOffset(Register.SP)),
                LDR(Register.R0, ImmOffset(Register.R0, Pair(Sign.PLUS, 4))),
                B(B.Mode.LINK, "free"),
                POP(Register.R0),
                B(B.Mode.LINK, "free"),
                POP(Register.PC)
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