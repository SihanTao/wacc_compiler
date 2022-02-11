package node.expr

import type.Type

class IdentNode(type: Type?, name: String) : ExprNode() {
    val name: String

    init {
        this.type = type
        this.name = name
    }
}