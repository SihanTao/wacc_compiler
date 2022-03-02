package node.stat

import SymbolTable
import node.Node
import node.expr.ExprNode

/**
     * abstract class for statement nodes. Any statement does not have a type
     */

abstract class StatNode : Node {

    var isReturned = false

    var scope: SymbolTable<ExprNode>? = null

}