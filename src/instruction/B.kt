package instruction

class B(
        val addr: String,
        var cond: Cond = Cond.AL
): ARM11Instruction {

    fun on(cond: Cond): B {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "B$cond $addr"
    }

}