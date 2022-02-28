package node

import node.expr.IdentNode
import node.stat.StatNode
import type.Type

class FuncNode(val identifier: String, val returnType: Type?, var functionBody: StatNode?, params: List<IdentNode?>?) : Node {
    private val parameters: List<IdentNode?>?

    constructor(ident: String, returnType: Type?, params: List<IdentNode?>?) : this(ident, returnType, null, params) {}

    init {
        parameters = params
    }

    val paramList: List<IdentNode?>?
        get() = parameters
}