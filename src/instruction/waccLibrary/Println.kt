package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class Println: WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf()

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("\\0")
        return listOf(
                LABEL("p_print_ln"),
                PUSH(Register.LR),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                ADD(Register.R0, Register.R0, 4),
                BL("puts"),
                MOV(Register.R0, 0),
                BL("fflush"),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}