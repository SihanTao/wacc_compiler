package node.expr

import symbolTable.Symbol
import backend.ASTVisitor
import type.Type

class IdentNode(type: Type?, val name: String, var symbol: Symbol?) : ExprNode() {
    /**
     * Represent an identifier node with its own type and name
     * Examples: int a
     */

    constructor(type: Type, name: String): this(type, name, null)

    init {
        this.type = type
        weight = 1
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitIdentNode(this)
    }
}