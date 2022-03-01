package instruction.waccLibrary

import instruction.ARM11Instruction

class FreePair(dataTable: Map<String, Int>) : WACCLibraryFunction(dataTable) {
    private val instructions: List<ARM11Instruction> = TODO()
    private val dependencies: List<WACCLibraryFunction> = TODO()


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}