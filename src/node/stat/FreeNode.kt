package node.stat

import node.expr.ExprNode

class FreeNode(
    /**
     * Represent a free statement, with <expr> being recorded
     * Example: free <expr>, free p (where p is a non-null pair)
    </expr></expr> */
    private val expr: ExprNode
) : StatNode()