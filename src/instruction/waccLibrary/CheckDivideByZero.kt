package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class CheckDivideByZero(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("DivideByZeroError: divide or modulo by zero\\n\\0")
        instructions = listOf(
                LABEL("p_check_divide_by_zero"),
                PUSH(Register.LR),
                CMP(Register.R1, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode"), cond=Cond.EQ),
                B(B.Mode.LINK, "p_throw_runtime_error", Cond.EQ),
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