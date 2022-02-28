package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent an if statment with ifBody and elseBody
 * store the result in endValue 
 */

class IfNode(
    private val condition: ExprNode, private val ifBody: StatNode?, private val elseBody: StatNode?
) : StatNode() {
    init {
        isReturned = endValue
    }

    private val endValue: Boolean
        get() {
            return ifBody!!.isReturned && elseBody!!.isReturned
        }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitIfNode(this)
    }
}