package node

import backend.ASTVisitor
import node.expr.IdentNode
import node.stat.StatNode
import type.Type

class FuncNode(
    val name: String,
    val returnType: Type?,
    var functionBody: StatNode?,
    val paramList: List<IdentNode>?
) : Node {
    constructor(
        name: String,
        returnType: Type?,
        params: List<IdentNode>?
    ) : this(name, returnType, null, params)

    fun paramListStackSize(): Int {
        var size = 0
        for (ident in paramList!!) {
            size += ident.type!!.size()
        }
        return size
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitFuncNode(this)
    }
}