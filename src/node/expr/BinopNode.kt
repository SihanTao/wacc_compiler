package node.expr

import backend.ASTVisitor
import type.BasicType
import type.BasicTypeEnum
import type.Utils.Binop

class BinopNode(var expr1: ExprNode, var expr2: ExprNode, var operator: Binop) : ExprNode() {
    /**
     * Represent basic binary operations 
     * can be of type integer or boolean 
     */

    init {
        type = when (operator) {
            Binop.PLUS, Binop.MINUS, Binop.MUL, Binop.DIV, Binop.MOD -> BasicType(BasicTypeEnum.INTEGER)
            else -> BasicType(BasicTypeEnum.BOOLEAN)
        }
        weight = expr1.weight() + expr2.weight() + 1
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitBinopNode(this)
    }
}