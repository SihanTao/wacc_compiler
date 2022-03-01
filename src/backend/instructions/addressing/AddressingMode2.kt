package backend.instructions.addressing

import backend.ARMRegister
import backend.instructions.operand.Immediate

class AddressingMode2 private constructor(/*
    addressing mode 2 pattern:
    OFFSET:                                     POSTINDEX:                                  PREINDEX:
    [<Rn>, #+/<immed_12>]                       [<Rn>], #+/<immed_12>                       [<Rn>], #+/<immed_12>
    [<Rn>]                                      [<Rn>]                                      [<Rn>]
    [<Rn>, +/-<Rm>]                             [<Rn>], +/-<Rm>                             [<Rn>, +/-<Rm>]!
    [<Rn>, +/-<Rm>, LSL/LSR/ASR/ROR #<immed_5>] [<Rn>], +/-<Rm>, LSL/LSR/ASR/ROR #<immed_5> [<Rn>, +/-<Rm>, LSL/LSR/ASR/ROR #<immed_5>]!
    [<Rn>, +/-<Rm>, RRX]                        [<Rn>], +/-<Rm>, RRX                        [<Rn>, +/-<Rm>, RRX]!
     */
    private val mode: AddrMode2,
    Rn: ARMRegister,
    Rm: ARMRegister?,
    operator: AddrMode2Operator?,
    immed: Immediate?
) : Addressing {
    private val Rn: ARMRegister?
    private val Rm: ARMRegister?
    private val operator: AddrMode2Operator?
    private val immed: Immediate?

    constructor(
        mode: AddrMode2,
        Rn: ARMRegister,
        Rm: ARMRegister?,
        operator: AddrMode2Operator?
    ) : this(mode, Rn, Rm, operator, null) {
    }


    constructor(mode: AddrMode2, Rn: ARMRegister) : this(
        mode,
        Rn,
        null,
        null,
        null
    ) {
    }

    constructor(mode: AddrMode2, Rn: ARMRegister, value: Int) : this(
        mode,
        Rn,
        null,
        null,
        Immediate(value)
    )

    override fun toString(): String {
        val str = StringBuilder()
        return when (mode) {
            AddrMode2.OFFSET -> {
                str.append(if (Rn != null) Rn else "")
                str.append(if (Rm != null) ", $Rm" else "")
                str.append(if (operator != null) ", " + operator.name + " " else "")
                str.append(if (immed != null && immed.value != 0) ", $immed" else "")
                "[$str]"
            }
            AddrMode2.PREINDEX -> {
                TODO("PREINDEX NOT IMPLEMENTED!")
            }
            AddrMode2.POSTINDEX -> {
                TODO("POSTINDEX NOT IMPLEMENTED!")

            }
        }
    }

    enum class AddrMode2Operator {
        LSL, LSR, ASR, ROR, RRX
    }

    enum class AddrMode2 {
        OFFSET, PREINDEX, POSTINDEX
    }

    init {
        this.Rn = Rn
        this.Rm = Rm
        this.operator = operator
        this.immed = immed
    }
}