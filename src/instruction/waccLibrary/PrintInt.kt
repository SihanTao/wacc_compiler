package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class PrintInt(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msg = codeGenerator.addDataElement("%d\\0")
        instructions = listOf(
                LABEL("p_print_int"),
                PUSH(Register.LR),
                MOV(Register.R1, Register.R0),
                LDR(Register.R0, StaticRef("msg_$msg")),
                ADD(Register.R0, Register.R0, 4),
                B(B.Mode.LINK, "printf"),
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