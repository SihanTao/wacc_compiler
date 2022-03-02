package instruction.addrMode2

import register.Register

class ImmPreIndex(val rn: Register, val sign: Sign, val rm: Pair<Sign,Register>): AddrMode2 {
    override fun toString(): String {
        return "[$rn, #${rm.first}${rm.second}]!"
    }
}

