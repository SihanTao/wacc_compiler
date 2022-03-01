package instruction

import instruction.addrMode2.AddrMode2
import register.ARM11Register

class Load(val mode: Mode = Mode.NORM,
           val cond: Cond = Cond.AL,
           val Rd: ARM11Register,
           val addr: AddrMode2): ARM11Instruction
{

    override fun toString(): String {
        return "LDR$cond${mode.asm} $Rd, $addr"
    }

    enum class Mode(val asm: String) {
        NORM(""), BYTE("B"), SIGNEDBYTE("SB")
    }
}

