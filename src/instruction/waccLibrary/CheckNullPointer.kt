package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class CheckNullPointer: WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf(ThrowRuntimeError())

    override fun getInstructions(codeGenerator:WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        return listOf(
                LABEL("p_check_null_pointer"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msgCode"), cond=Cond.EQ),
                BL("p_throw_runtime_error", Cond.EQ),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}