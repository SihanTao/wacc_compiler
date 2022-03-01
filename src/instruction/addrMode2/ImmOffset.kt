package instruction.addrMode2

import register.ARM11Register

class ImmOffset(val Rn: ARM11Register, val offset: Pair<Sign,Int>): AddrMode2 {
    constructor(Rn: ARM11Register): this(Rn, Pair(Sign.PLUS, 0))
    override fun toString(): String {
        if (offset.second == 0) {
            return "[$Rn]"
        } else {
            return "[$Rn, #${offset.first}${offset.second}]"
        }
    }
}