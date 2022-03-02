package backend.instructions.unopAndBinop

import backend.ARMRegister
import backend.Cond
import backend.instructions.*
import backend.instructions.operand.Operand2
import type.Utils
import java.lang.UnsupportedOperationException
import javax.accessibility.AccessibleStateSet
import kotlin.jvm.internal.PropertyReference1

class Operator {
    companion object {
        fun addDivMod(
            Rd: ARMRegister,
            Rn: ARMRegister,
            op: Utils.Binop
        ): List<Instruction> {
            val res: MutableList<Instruction> = mutableListOf(
                Mov(ARMRegister.R0, Operand2(Rd)),
                Mov(ARMRegister.R1, Operand2(Rn)),
                BL(RuntimeErrorInstruction.CHECK_DIVIDE_BY_ZERO.toString())
            )

            if (op == Utils.Binop.DIV) {
                res.add(BL("__aeabi_idiv"))
                res.add(Mov(Rd, Operand2(ARMRegister.R0)))
            } else {
                // MOD
                res.add(BL("__aeabi_idivmod"))
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