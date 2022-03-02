package node.expr

import backend.ASTVisitor
import type.BasicType
import type.BasicTypeEnum
import type.Utils

class UnopNode(var expr: ExprNode, var operator: Utils.Unop) : ExprNode() {

    /**
     * Represent a unary operator which has a single sub-expression
     * the opearators can be !, -, len, ord, chr
     * Examples: !true, -1
     */

    init {
        type = when (operator) {
            Utils.Unop.NOT -> BasicType(BasicTypeEnum.BOOLEAN)
            Utils.Unop.LEN, Utils.Unop.MINUS, Utils.Unop.ORD -> BasicType(BasicTypeEnum.INTEGER)
            Utils.Unop.CHR -> BasicType(BasicTypeEnum.CHAR)
        }
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitUnopNode(this)
    }
}