package backend.instructions.unopAndBinop

import backend.register.ARMRegister
import backend.utils.Cond
import backend.instructions.*
import backend.utils.Operand2
import backend.utils.RuntimeErrorInstructionHelper
import type.Utils
import java.lang.UnsupportedOperationException

class Operator {
    companion object {
        private const val DIVIDE_LABEL_MSG = "__aeabi_idiv"
        private const val MOD_LABEL_MSG = "__aeabi_idivmod"

        fun addDivMod(
            Rd: ARMRegister,
            Rn: ARMRegister,
            op: Utils.Binop
        ): List<Instruction> {
            val res: MutableList<Instruction> = mutableListOf(
                Mov(ARMRegister.R0, Operand2(Rd)),
                Mov(ARMRegister.R1, Operand2(Rn)),
                BL("${RuntimeErrorInstructionHelper.CHECK_DIVIDE_BY_ZERO}")
            )

            if (op == Utils.Binop.DIV) {
                res.add(BL(DIVIDE_LABEL_MSG))
                res.add(Mov(Rd, Operand2(ARMRegister.R0)))
            } else {
                // MOD
                res.add(BL(MOD_LABEL_MSG))
                res.add(Mov(Rd, Operand2(ARMRegister.R1)))
            }

            return res.toList()
        }

        fun addCompare(
            Rd: ARMRegister,
            Rn: ARMRegister,
            op: Utils.Binop
        ): List<Instruction> {
            val res: MutableList<Instruction> = mutableListOf()
            res.add(Cmp(Rd, Operand2(Rn)))
            when (op) {
                Utils.Binop.GREATER -> {
                    res.add(Mov(Cond.GT, Rd, 1))
                    res.add(Mov(Cond.LE, Rd, 0))
                }
                Utils.Binop.GREATER_EQUAL -> {
                    res.add(Mov(Cond.GE, Rd, 1))
                    res.add(Mov(Cond.LT, Rd, 0))
                }
                Utils.Binop.LESS -> {
                    res.add(Mov(Cond.LT, Rd, 1))
                    res.add(Mov(Cond.GE, Rd, 0))
                }
                Utils.Binop.LESS_EQUAL -> {
                    res.add(Mov(Cond.LE, Rd, 1))
                    res.add(Mov(Cond.GT, Rd, 0))
                }
                Utils.Binop.EQUAL -> {
                    res.add(Mov(Cond.EQ, Rd, 1))
                    res.add(Mov(Cond.NE, Rd, 0))
                }
                Utils.Binop.INEQUAL -> {
                    res.add(Mov(Cond.NE, Rd, 1))
                    res.add(Mov(Cond.EQ, Rd, 0))
                }
                else -> throw UnsupportedOperationException("These cases should have been handled.")
            }
            return res
        }
    }
}