package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a declaration statement
 * has its own identifier and also a right-hand-side note which is an expression node
 * Examples: int i = 1
 */

class DeclareStatNode(
    val identifier: String, val rhs: ExprNode?
) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitDeclareStatNode(this)
    }
}