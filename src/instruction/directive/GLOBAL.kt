package instruction.directive

import instruction.ARM11Instruction

class GLOBAL(val symbol: String): ARM11Instruction {
    override fun toString(): String {
        return ".global $symbol"
    }

}