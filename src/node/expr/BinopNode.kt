package node.expr

import type.BasicType
import type.BasicTypeEnum
import type.Utils.Binop

class BinopNode(var expr1: ExprNode, var expr2: ExprNode, var operator: Binop) : ExprNode() {
    /**
     * Represent basic binary operations 
     * can be of type integer or boolean 
     */

    init {
        when (operator) {
            Binop.PLUS, Binop.MINUS, Binop.MUL, Binop.DIV, Binop.MOD -> type = BasicType(BasicTypeEnum.INTEGER)
            else -> type = BasicType(BasicTypeEnum.BOOLEAN)
        }
    }
}