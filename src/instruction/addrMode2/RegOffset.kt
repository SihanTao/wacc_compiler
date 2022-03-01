package instruction.addrMode2

import register.ARM11Register

class RegOffset(val Rn: ARM11Register, val Rm: Pair<Sign, ARM11Register>): AddrMode2 {
    override fun toString(): String {
        return "[$Rn, #${Rm.first}${Rm.second}]"
    }
}
