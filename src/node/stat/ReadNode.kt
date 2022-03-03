package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a read statement with an input expression
 */

class ReadNode(val inputExpr: ExprNode) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitReadNode(this)
    }
}