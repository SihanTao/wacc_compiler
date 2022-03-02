package instruction

import instruction.addressing_mode.AddressingMode
import register.Register

class STR(val rd: Register,
          val addr: AddressingMode,
          var cond: Cond = Cond.AL): ARM11Instruction
{

    fun on(cond: Cond): STR {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "STR$cond $rd, $addr"
    }
}