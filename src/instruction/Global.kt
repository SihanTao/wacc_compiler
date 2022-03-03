package instruction

class Global(val symbol: String): ARM11Instruction {
    override fun toString(): String {
        return ".global $symbol"
    }

}