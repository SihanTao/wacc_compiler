package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent an assignment node with left-hand-side and right-hand-side
 * both left-hand-side and right-hand-side are expression nodes
 * Examples:  false 
 *            /   \
 *           1     2
 */

class AssignNode(
    val lhs: ExprNode?, val rhs: ExprNode?
) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitAssignNode(this)
    }
}