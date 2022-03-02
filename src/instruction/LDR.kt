package instruction

import instruction.addressing_mode.AddressingMode
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.StaticRef
import register.Register

class LDR(val rd: Register,
          val addr: AddressingMode,
          var cond: Cond = Cond.AL,): ARM11Instruction
{
    constructor(rd: Register, `=`: String):
            this(rd, StaticRef(`=`))

    fun on(cond: Cond): LDR {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "LDR$cond $rd, $addr"
    }
}

