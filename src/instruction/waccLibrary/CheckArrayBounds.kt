package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.StaticRef
import register.Register

class CheckArrayBounds(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode1 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: negative index\\n\\0")
        val msgCode2 = codeGenerator.addDataElement("ArrayIndexOutOfBoundsError: index too large\\n\\0")
        instructions = listOf(
                Label("p_check_array_bounds"),
                Push(Register.LR),
                Compare(Register.R0, 0),
                Load(Register.R0, StaticRef("msg_$msgCode1"), cond= Cond.LT),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.LT),
                Load(Register.R1, ImmOffset(Register.R1)),
                Load(Register.R0, StaticRef("msg_$msgCode2"), cond= Cond.CS),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.CS),
                Pop(Register.PC),
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