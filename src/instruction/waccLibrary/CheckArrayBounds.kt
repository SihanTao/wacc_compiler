package instruction.waccLibrary

import WACCCodeGenerator
import WACCCodeGeneratorVisitor
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.RegOffset
import instruction.addrMode2.StaticRef
import register.ARM11Register

class CheckArrayBounds(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode1 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: negative index\\n\\0")
        val msgCode2 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: index too large\\n\\0")
        instructions = listOf(
                Label("p_check_array_bounds"),
                Push(ARM11Register.LR),
                Compare(ARM11Register.R0, 0),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode1"), cond= Cond.LT),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.LT),
                Load(ARM11Register.R1, ImmOffset(ARM11Register.R1)),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode2"), cond= Cond.CS),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.CS),
                Pop(ARM11Register.PC),
        )
        dependencies = listOf(ThrowRuntimeError(codeGenerator))
    }


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}