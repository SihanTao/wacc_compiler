package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a print statement
 */

class PrintNode(
    val expr: ExprNode?
) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitPrintNode(this)
    }
}