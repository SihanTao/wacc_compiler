package node.stat

import node.expr.ExprNode

/**
 * Represent an read statement with an input expression 
 */

class ReadNode(val inputExpr: ExprNode) : StatNode()