package node.expr

import backend.ASTVisitor
import type.BasicTypeEnum
import type.BasicType

class BoolNode(

    /**
     * Represent a boolean
     * Examples: true, false
     */
    val value: Boolean
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.BOOLEAN)
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitBoolNode(this)
    }
}