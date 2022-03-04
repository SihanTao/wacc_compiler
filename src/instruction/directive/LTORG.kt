package instruction.directive

import instruction.ARM11Instruction

class LTORG: ARM11Instruction {
    override fun toString(): String {
        return ".ltorg"
    }
}