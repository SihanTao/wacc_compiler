package node.expr

import node.Node
import type.Type

abstract class ExprNode : Node {
    /**
     * abstract class for expression nodes. Every expression node has a type
     */
    open var type: Type? = null
    protected var weight = 0

    fun weight(): Int {
        return weight
    }
}