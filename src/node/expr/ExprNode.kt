package node.expr

import node.Node
import type.Type

abstract class ExprNode : Node {
    /**
     * abstract class for expression nodes. Every expression node has a type
     */
    var type: Type? = null


}