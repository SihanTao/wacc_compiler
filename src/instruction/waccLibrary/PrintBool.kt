package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class PrintBool : WACCLibraryFunction() {
    private val dependencies: List<WACCLibraryFunction> = listOf()

    override fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction> {
        val msg1 = codeGenerator.addDataElement("true\\0")
        val msg2 = codeGenerator.addDataElement("false\\0")
        return listOf(
                LABEL("p_print_bool"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msg1")).on(Cond.NE),
                LDR(Register.R0, StaticRef("msg_$msg2")).on(Cond.EQ),
                ADD(Register.R0, Register.R0, 4),
                BL("printf"),
                MOV(Register.R0, 0),
                BL( "fflush"),
                POP(Register.PC),
        )
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}