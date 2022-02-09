package node.expr

import node.Node
import node.TypeNode

abstract class ExprNode : Node {
    /**
     * abstract class for expression nodes. Every expression node has a type
     */
    protected var type: TypeNode? = null


}