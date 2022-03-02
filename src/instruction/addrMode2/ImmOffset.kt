package instruction.addrMode2

import register.Register

class ImmOffset(val rn: Register, val offset: Pair<Sign,Int>): AddrMode2 {
    constructor(rn: Register): this(rn, Pair(Sign.PLUS, 0))
    override fun toString(): String {
        if (offset.second == 0) {
            return "[$rn]"
        } else {
            return "[$rn, #${offset.first}${offset.second}]"
        }
    }
}