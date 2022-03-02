package node.stat

import backend.ASTVisitor
import node.expr.ExprNode

/**
 * Represent a free statement with <expr> being recorded
 * Example: free <expr>, free p (where p is a non-null pair)
</expr></expr> */

class FreeNode(
    val expr: ExprNode
) : StatNode() {
    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitFreeNode(this)
    }
}