package instruction

import instruction.addressing_mode.AddressingMode
import instruction.addressing_mode.StaticRef
import register.Register

class LDRB(val rd: Register,
          val addr: AddressingMode,
          var cond: Cond = Cond.AL,): ARM11Instruction
{
    constructor(rd: Register, `=`: String):
            this(rd, StaticRef(`=`))

    fun on(cond: Cond): LDRB {
        this.cond = cond
        return this
    }

    override fun toString(): String {
        return "LDR${cond}B $rd, $addr"
    }
}