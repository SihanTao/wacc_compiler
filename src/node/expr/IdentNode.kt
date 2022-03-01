package node.expr

import Symbol
import type.Type

class IdentNode(type: Type?, val name: String, var symbol: Symbol?) : ExprNode() {
    /**
     * Represent an identifier node with its own type and name
     * Examples: int a
     */

    constructor(type: Type, name: String): this(type, name, null)

    init {
        this.type = type
    }
}