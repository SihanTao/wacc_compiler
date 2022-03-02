package instruction

class BL(
        val addr: String,
        var cond: Cond = Cond.AL
): ARM11Instruction {

    fun on(cond: Cond): BL {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "BL$cond $addr"
    }
}