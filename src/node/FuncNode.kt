package node

import node.expr.IdentNode
import node.stat.StatNode
import type.Type

class FuncNode(val returnType: Type?, var functionBody: StatNode?, params: List<IdentNode?>?) : Node {
    private val parameters: List<IdentNode?>?

    constructor(returnType: Type?, params: List<IdentNode?>?) : this(returnType, null, params) {}

    init {
        parameters = params
    }

    val paramList: List<IdentNode?>?
        get() = parameters
}