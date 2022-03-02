package node

import node.expr.IdentNode
import node.stat.StatNode
import type.Type

class FuncNode(val returnType: Type?, var functionBody: StatNode?, val paramList: List<IdentNode>?) : Node {
    constructor(returnType: Type?, params: List<IdentNode>?) : this(returnType, null, params) {}
}