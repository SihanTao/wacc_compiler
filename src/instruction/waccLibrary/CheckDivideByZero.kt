package instruction.waccLibrary

import WACCCodeGenerator
import instruction.*
import instruction.addrMode2.StaticRef
import register.ARM11Register

class CheckDivideByZero(codeGenerator: WACCCodeGenerator) : WACCLibraryFunction(codeGenerator) {
    private val instructions: List<ARM11Instruction>
    private val dependencies: List<WACCLibraryFunction>

    init {
        val msgCode = codeGenerator.addDataElement("DivideByZeroError: divide or modulo by zero\\n\\0")
        instructions = listOf(
                Label("p_check_divide_by_zero"),
                Push(ARM11Register.LR),
                Compare(ARM11Register.R1, 0),
                Load(ARM11Register.R0, StaticRef("msg_$msgCode"), cond=Cond.EQ),
                Branch(Branch.Mode.LINK, "p_throw_runtime_error", Cond.EQ),
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