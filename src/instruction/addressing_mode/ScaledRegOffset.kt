package instruction.addressing_mode

import instruction.shifter_operand.Shift
import register.Register

class ScaledRegOffset(val Rn: Register, val Rm: Pair<Sign, Register>, val shift: Shift, val imm: Int): AddressingMode {

    override fun toString(): String {
        return "[$Rn, ${Rm.first}${Rm.second}, $shift #$imm]"
    }
}