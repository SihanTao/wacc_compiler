package instruction

import instruction.shiftOperand.ShifterOperand
import register.ARM11Register

class SMULL(val RdLo: ARM11Register, val RdHi: ARM11Register,
            val Rm: ARM11Register, val Rs: ARM11Register,
            val cond: Cond = Cond.AL,
            val S: Boolean = false,
):ARM11Instruction  {
    override fun toString(): String {
        val s = if (S) "S" else ""
        return "SMULL$cond$s $RdLo, $RdHi, $Rm, $Rs"
    }
}