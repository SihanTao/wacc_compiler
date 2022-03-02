package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class Println(codeGenerator: WACCCodeGenerator): WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("\\0")
        instructions = listOf(
                LABEL("p_print_ln"),
                PUSH(Register.LR),
                LDR(Register.R0, StaticRef("msg_$msgCode")),
                ADD(Register.R0, Register.R0, 4),
                B(B.Mode.LINK, "puts"),
                MOV(Register.R0, 0),
                B(B.Mode.LINK, "fflush"),
                POP(Register.PC),
        )
        dependencies = listOf()
    }


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}