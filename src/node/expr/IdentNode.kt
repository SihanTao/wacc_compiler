package node.expr

import type.Type

class IdentNode(type: Type?, name: String) : ExprNode() {
    /**
     * Represent an identifier node with its own type and name
     * Examples: int a
     */

    val name: String

    init {
        this.type = type
        this.name = name
    }
}