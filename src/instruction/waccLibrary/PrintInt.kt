package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class PrintInt : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf()


    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msg = codeGenerator.addDataElement("%d\\0")
        return listOf(
                LABEL("p_print_int"),
                PUSH(Register.LR),
                MOV(Register.R1, Register.R0),
                LDR(Register.R0, StaticRef("msg_$msg")),
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