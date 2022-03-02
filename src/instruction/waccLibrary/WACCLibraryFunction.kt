package instruction.waccLibrary

import WACCCodeGenerator
import instruction.ARM11Instruction
import org.antlr.v4.codegen.CodeGenerator

abstract class WACCLibraryFunction {
    abstract fun getInstructions(codeGenerator: WACCCodeGenerator): List<ARM11Instruction>
    abstract fun getDependencies(): List<WACCLibraryFunction>

    override fun equals(other: Any?): Boolean {
        return other?.javaClass!!.equals(this.javaClass)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}