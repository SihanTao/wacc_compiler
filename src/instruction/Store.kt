package instruction

import instruction.addrMode2.AddrMode2
import register.ARM11Register

class Store(val Rd: ARM11Register,
           val addr: AddrMode2,
            val mode: Mode = Mode.NORM,
            val cond: Cond = Cond.AL,): ARM11Instruction
{

    override fun toString(): String {
        return "STR$cond${mode.asm} $Rd, $addr"
    }

    enum class Mode(val asm: String) {
        NORM(""), BYTE("B")
    }
}