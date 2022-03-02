package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class CheckDivideByZero : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(ThrowRuntimeError())

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
    }


    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("DivideByZeroError: divide or modulo by zero\\n\\0")
        return listOf(
                LABEL("p_check_divide_by_zero"),
                PUSH(Register.LR),
                CMP(Register.R1, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode")).on(Cond.EQ),
                BL("p_throw_runtime_error").on(Cond.EQ),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}