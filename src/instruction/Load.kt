package instruction

import instruction.addrMode2.AddrMode2
import instruction.addrMode2.ImmOffset
import instruction.addrMode2.staticRef
import instruction.shiftOperand.Immediate
import register.ARM11Register

class Load(val mode: Mode = Mode.NORM,
           val cond: Cond = Cond.AL,
           val Rd: ARM11Register,
           val addr: AddrMode2): ARM11Instruction
{
    constructor(Rd: ARM11Register, addr: String): this(Rd=Rd, addr=staticRef(addr))
    constructor(Rd: ARM11Register, imm: Int): this(Rd=Rd, addr=ImmOffset(imm))

    override fun toString(): String {
        return "LDR$cond${mode.asm} $Rd, $addr"
    }

    enum class Mode(val asm: String) {
        NORM(""), BYTE("B"), SIGNEDBYTE("SB")
    }
}

