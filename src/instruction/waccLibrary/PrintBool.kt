package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addressing_mode.StaticRef
import register.Register

class PrintBool(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msg1 = codeGenerator.addDataElement("true\\0")
        val msg2 = codeGenerator.addDataElement("false\\0")
        instructions = listOf(
                LABEL("p_print_bool"),
                PUSH(Register.LR),
                CMP(Register.R0, 0),
                LDR(Register.R0, StaticRef("msg_$msg1"), cond=Cond.NE),
                LDR(Register.R0, StaticRef("msg_$msg2"), cond=Cond.NE),
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