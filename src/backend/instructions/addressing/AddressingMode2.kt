package backend.instructions.addressing

import backend.register.ARMRegister
import backend.utils.Immediate

class AddressingMode2 private constructor(/*
    addressing mode 2 pattern:
    OFFSET:                                      PRE_INDEX:
    [<Rn>, #+/<immed_12>]                        [<Rn>], #+/<immed_12>
    [<Rn>]                                       [<Rn>]
    [<Rn>, +/-<Rm>]                              [<Rn>, +/-<Rm>]!
    [<Rn>, +/-<Rm>, LSL/LSR/ASR/ROR #<immed_5>]  [<Rn>, +/-<Rm>, LSL/LSR/ASR/ROR #<immed_5>]!
    [<Rn>, +/-<Rm>, RRX]                         [<Rn>, +/-<Rm>, RRX]!
     */
    mode: AddrMode2,
    Rn: ARMRegister,
    Rm: ARMRegister?,
    operator: AddrMode2Operator?,
    immed: Immediate?
) : Addressing {
    private val rn: ARMRegister?
    private val rm: ARMRegister?
    private val operator: AddrMode2Operator?
    private val immediate: Immediate?
    private var mode: AddrMode2

    constructor(Rn: ARMRegister) : this(AddrMode2.OFFSET, Rn, null, null, null)

    constructor(mode: AddrMode2, Rn: ARMRegister, value: Int) : this(
        mode,
        Rn,
        null,
        null,
        Immediate(value)
    )

    constructor(Rn: ARMRegister, offset: Int) : this(
        AddrMode2.OFFSET,
        Rn,
        null,
        null,
        Immediate(offset)
    )

    override fun toString(): String {
        val str = StringBuilder()
        return when (mode) {
            AddrMode2.OFFSET -> {
                str.append(rn ?: "")
                str.append(if (rm != null) ", $rm" else "")
                str.append(if (operator != null) ", " + operator.name + " " else "")
                str.append(if (immediate != null && immediate.value != 0) ", $immediate" else "")
                "[$str]"
            }
            AddrMode2.PRE_INDEX -> {
                str.append(if (rn != null) "[$rn" else "")
                if (rm == null) {
                    str.append(if (immediate != null) ", $immediate" else "]")
                } else {
                    str.append(", $rm")
                    str.append(if (operator != null) ", " + operator.name + " " else "")
                    str.append(if (immediate != null) ", $immediate" else "")
                }
                return "$str]!"
            }
        }
    }

    enum class AddrMode2Operator {
        LSL, LSR, ASR, ROR, RRX
    }

    enum class AddrMode2 {
        OFFSET, PRE_INDEX
    }

    init {
        this.rn = Rn
        this.rm = Rm
        this.operator = operator
        this.immediate = immed
        this.mode = mode
    }
}