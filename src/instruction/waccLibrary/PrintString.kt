package instruction.waccLibrary

import WACCCodeGenerator
import instruction.ARM11Instruction
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.StaticRef
import register.Register

class PrintString(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>;
    private val dependencies: List<WACCLibraryFunction>;

    init {
        val msgCode = codeGenerator.addDataElement("%.*s\\0")
        instructions = listOf(
                Label("p_print_string"),
                Push(Register.LR),
                Load(Register.R1, ImmOffset(Register.R0)),
                Add(Register.R2, Register.R0, 4),
                Load(Register.R0, StaticRef("msg_$msgCode")),
                Add(Register.R0, Register.R0, 4),
                Branch(Branch.Mode.LINK, "printf"),
                Move(Register.R0, 0),
                Branch(Branch.Mode.LINK, "fflush"),
                Pop(Register.PC),
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