package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class ReadInt() : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf()

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msgCode = codeGenerator.addDataElement("%d\\0")
        return listOf(
                LABEL("p_read_int"),
                PUSH(Register.LR),
                MOV(Register.R1, Register.R0),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                ADD(Register.R0, Register.R0, 4),
                BL("scanf"),
                POP(Register.PC)
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}