package node.stat

import node.expr.ExprNode

/**
 * Represent a free statement with <expr> being recorded
 * Example: free <expr>, free p (where p is a non-null pair)
</expr></expr> */

class FreeNode(
    private val expr: ExprNode
) : StatNode()