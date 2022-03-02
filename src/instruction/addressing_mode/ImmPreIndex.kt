package instruction.addressing_mode

import register.Register

class ImmPreIndex(val Rn: Register, val offset: Pair<Sign,Int>): AddressingMode {
    constructor(rn: Register): this(rn, Pair(Sign.PLUS, 0))

    override fun toString(): String {
        if (offset.second == 0) {
            return "[$Rn]!"
        } else {
            return "[$Rn, #${offset.first}${offset.second}]!"
        }
    }
}

