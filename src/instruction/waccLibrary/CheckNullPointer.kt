package instruction.waccLibrary

import instruction.ARM11Instruction

class CheckNullPointer: WACCLibraryFunction() {
    private val instructions: List<ARM11Instruction> = TODO()
    private val dependencies: List<WACCLibraryFunction> = TODO()


    override fun getInstructions(): List<ARM11Instruction> {
        return instructions
    }

    override fun getDependencies(): List<WACCLibraryFunction> {
        return dependencies
    }

}