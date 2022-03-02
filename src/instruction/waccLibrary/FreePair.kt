package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.Sign
import instruction.addrMode2.StaticRef
import instruction.shiftOperand.Immediate
import register.ARM11Register

class FreePair(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("NullReferenceError: dereference a null reference\\n\\0")
        instructions = listOf(
                Label("p_free_pair"),
                Push(ARM11Register.LR),
                Compare(ARM11Register.R0, 0),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode")),
                Branch("p_throw_runtime_error", cond=Cond.EQ),
                Load(ARM11Register.R0, ImmOffset(ARM11Register.R0)),
                Branch(Branch.Mode.LINK, "free"),
                Load(ARM11Register.R0, ImmOffset(ARM11Register.SP)),
                Load(ARM11Register.R0, ImmOffset(ARM11Register.R0, Pair(Sign.PLUS, 4))),
                Branch(Branch.Mode.LINK, "free"),
                Pop(ARM11Register.R0),
                Branch(Branch.Mode.LINK, "free"),
                Pop(ARM11Register.PC)
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