package backend.instructions.unopAndBinop

import backend.ARMRegister
import backend.Cond
import backend.instructions.*
import backend.instructions.operand.Operand2
import type.Utils

class Operator {
    companion object {
        fun addDivMod(
            Rd: ARMRegister,
            Rn: ARMRegister,
            op: Utils.Binop
        ): List<Instruction> {
            return listOf(
                Mov(ARMRegister.R0, Operand2(Rd)),
                Mov(ARMRegister.R1, Operand2(Rn)),
                BL(RuntimeErrorInstruction.CHECK_DIVIDE_BY_ZERO.toString()),
                if (op == Utils.Binop.DIV) {
                    BL("__aeabi_idiv")
                } else {
                    // MOD
                    BL("__aeabi_idivmod")
                },
                Mov(Rd, Operand2(ARMRegister.R1))
            )
        }

        fun addCompare(
            Rd: ARMRegister,
            Rn: ARMRegister,
            op: Utils.Binop
        ): List<Instruction> {
            val res: MutableList<Instruction> = mutableListOf()
            when (op) {
                Utils.Binop.GREATER -> {
                    res.add(Mov(Cond.GT, Rd, 1))
                    res.add(Mov(Cond.LE, Rd, 0))
                }
                Utils.Binop.GREATER_EQUAL -> {
                    res.add(Mov(Cond.GE, Rd, 1))
                    res.add(Mov(Cond.LT, Rd, 0))
                }
                else -> TODO()
            }
            return res
        }
    }
}