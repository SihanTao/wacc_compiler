package instruction.addressing_mode

import register.Register

class ImmOffset(val Rn: Register, val offset: Pair<Sign,Int>): AddressingMode {
    constructor(Rn: Register): this(Rn, Pair(Sign.PLUS, 0))
    constructor(Rn: Register, offset: Int): this(Rn, Pair(Sign.PLUS, offset))

    override fun toString(): String {
        if (offset.second == 0) {
            return "[$Rn]"
        } else {
            return "[$Rn, #${offset.first}${offset.second}]"
        }
    }
}