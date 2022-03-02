package instruction.addressing_mode

import register.Register

class RegOffset(val Rn: Register, val Rm: Pair<Sign, Register>): AddressingMode {

    override fun toString(): String {
        return "[$Rn, ${Rm.first}${Rm.second}]"
    }
}
