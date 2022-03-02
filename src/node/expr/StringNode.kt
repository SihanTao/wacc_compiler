package node.expr

import backend.ASTVisitor
import type.BasicType
import type.BasicTypeEnum

class StringNode(val string: String) : ExprNode() {

    /**
     * Represent a string
     * Examples: "string"
     */

    val length: Int = string.length

    init {
        type = BasicType(BasicTypeEnum.STRING)
        weight = 1
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitStringNode(this)
    }
}