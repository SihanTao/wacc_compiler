package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a println statement
 */

class PrintlnNode(
    val expr: ExprNode?
) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitPrintlnNode(this)
    }
}
