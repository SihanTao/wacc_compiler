package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a return statement
 */

class ReturnNode(private val expr: ExprNode) : StatNode() {
    init {
        isReturned = true
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitReturnNode(this)
    }
}