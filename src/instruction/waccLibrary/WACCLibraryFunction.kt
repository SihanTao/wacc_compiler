package instruction.waccLibrary

import instruction.ARM11Instruction

abstract class WACCLibraryFunction(dataTable: Map<String, Int>) {
    abstract fun getInstructions(): List<ARM11Instruction>
    abstract fun getDependencies(): List<WACCLibraryFunction>

    override fun equals(other: Any?): Boolean {
        return other?.javaClass!!.equals(this.javaClass)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}