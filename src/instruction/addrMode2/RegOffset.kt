package instruction.addrMode2

import register.Register

class RegOffset(val rn: Register, val rm: Pair<Sign, Register>): AddrMode2 {

    override fun toString(): String {
        return "[$rn, #${rm.first}${rm.second}]"
    }
}
