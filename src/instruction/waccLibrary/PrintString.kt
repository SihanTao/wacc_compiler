package instruction.waccLibrary

import WACCCodeGenerator
import instruction.ARM11Instruction
import instruction.*
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.StaticRef
import register.Register

class PrintString: WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf()

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("%.*s\\0")
        return listOf(
                LABEL("p_print_string"),
                PUSH(Register.LR),
                LDR(Register.R1, ImmOffset(Register.R0)),
                ADD(Register.R2, Register.R0, 4),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                ADD(Register.R0, Register.R0, 4),
                BL("printf"),
                MOV(Register.R0, 0),
                BL("fflush"),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}