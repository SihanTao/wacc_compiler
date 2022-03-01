package instruction.waccLibrary

import WACCCodeGenerator
import instruction.ARM11Instruction
import instruction.*
import instruction.addrMode2.ImmOffset
import org.antlr.v4.codegen.CodeGenerator
import register.ARM11Register

class PrintString(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>;
    private val dependencies: List<WACCLibraryFunction>;
    val msgCode: Int

    init {
        msgCode = codeGenerator.addDataElement("\"%.*s\\0\"")
        instructions = listOf(
                Label("p_print_string"),
                Push(ARM11Register.LR),
                Load(ARM11Register.R1, ImmOffset(ARM11Register.R0)),
                Add(ARM11Register.R2, ARM11Register.R0, 4),
                Load(ARM11Register.R0, "msg_$msgCode"),
                Add(ARM11Register.R0, ARM11Register.R0, 4),
                Branch(Branch.Mode.LINK, "printf"),
                Move(ARM11Register.R0, 0),
                Branch(Branch.Mode.LINK, "fflush"),
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