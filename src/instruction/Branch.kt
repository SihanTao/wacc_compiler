package instruction

class Branch(
        val addr: String,
        val L: Mode = Mode.NORM,
        val cond: Cond = Cond.AL
): ARM11Instruction {

    constructor(L: Mode, str: String): this(L=L, addr=str)
    constructor(L: Mode, str: String, cond:Cond): this(L=L, addr=str, cond=cond)

    override fun toString(): String {
        return "B$L$cond $addr"
    }

    enum class Mode(val str: String){
        NORM(""), LINK("L")
    }

}