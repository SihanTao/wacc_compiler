package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.ARM11Register

class ReadInt(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("%d\\0")
        instructions = listOf(
                Label("p_read_int"),
                Push(ARM11Register.LR),
                Move(ARM11Register.R1, ARM11Register.R0),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode")),
                Add(ARM11Register.R0, ARM11Register.R0, 4),
                Branch(Branch.Mode.LINK, "scanf"),
                Pop(ARM11Register.PC)
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