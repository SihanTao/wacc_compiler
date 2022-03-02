package node.expr

import backend.ASTVisitor
import type.BasicType
import type.BasicTypeEnum

class IntNode(
    /**
     * Represent and integer node
     * Examples: 1, 2, 3
     */

    val value: Int
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.INTEGER)
        weight = 1
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitIntNode(this)
    }
}