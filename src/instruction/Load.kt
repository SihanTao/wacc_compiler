package instruction

import instruction.addrMode2.AddrMode2
import register.Register

class Load(val rd: Register,
           val addr: AddrMode2,
           val mode: Mode = Mode.NORM,
           val cond: Cond = Cond.AL,): ARM11Instruction
{

    override fun toString(): String {
        return "LDR$cond${mode.asm} $rd, $addr"
    }

    enum class Mode(val asm: String) {
        NORM(""), BYTE("B"), SIGNEDBYTE("SB")
    }
}

