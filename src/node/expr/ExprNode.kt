package node.expr

import node.Node
import type.TypeNode

abstract class ExprNode : Node {
    /**
     * abstract class for expression nodes. Every expression node has a type
     */
    var type: TypeNode? = null


}