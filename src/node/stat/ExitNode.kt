package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent an exit statement 
 * set return to true and exit
 */

class ExitNode(val exitCode: ExprNode) : StatNode() {
    init {
        isReturned = true
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitExitNode(this)
    }
}
