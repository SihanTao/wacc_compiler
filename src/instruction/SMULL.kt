package instruction

import register.Register

class SMULL(val rdLo: Register, val rdHi: Register,
            val rm: Register, val rs: Register,
            val cond: Cond = Cond.AL,
            val S: Boolean = false,
):ARM11Instruction  {
    override fun toString(): String {
        val s = if (S) "S" else ""
        return "SMULL$cond$s $rdLo, $rdHi, $rm, $rs"
    }
}