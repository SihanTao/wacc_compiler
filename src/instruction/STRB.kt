package instruction

import instruction.addressing_mode.AddressingMode
import register.Register

class STRB(val rd: Register,
          val addr: AddressingMode,
          var cond: Cond = Cond.AL): ARM11Instruction
{

    fun on(cond: Cond): STRB {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "STR${cond}B $rd, $addr"
    }
}