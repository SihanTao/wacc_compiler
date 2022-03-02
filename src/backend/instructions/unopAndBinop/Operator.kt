package backend.instructions.unopAndBinop

import backend.ARMRegister
import backend.instructions.BL
import backend.instructions.Instruction
import backend.instructions.Mov
import backend.instructions.RuntimeErrorInstruction
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
    }
}