package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.Sign
import instruction.addrMode2.StaticRef
import register.Register

class FreePair(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        instructions = listOf(
                Label("p_free_pair"),
                Push(Register.LR),
                Compare(Register.R0, 0),
                Load(Register.R0, StaticRef("msg_$msgCode")),
                Branch("p_throw_runtime_error", cond=Cond.EQ),
                Load(Register.R0, ImmOffset(Register.R0)),
                Branch(Branch.Mode.LINK, "free"),
                Load(Register.R0, ImmOffset(Register.SP)),
                Load(Register.R0, ImmOffset(Register.R0, Pair(Sign.PLUS, 4))),
                Branch(Branch.Mode.LINK, "free"),
                Pop(Register.R0),
                Branch(Branch.Mode.LINK, "free"),
                Pop(Register.PC)
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