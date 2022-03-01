package instruction.addrMode2

import register.ARM11Register

class ImmPreIndex(val Rn: ARM11Register, val sign: Sign ,val Rm: Pair<Sign,ARM11Register>): AddrMode2 {
    override fun toString(): String {
        return "[$Rn, #${Rm.first}${Rm.second}]!"
    }
}

