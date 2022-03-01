package node.expr

import ErrorHandler.Companion.indexOutOfBoundError
import Symbol
import backend.ASTVisitor
import type.ArrayType
import type.Type

class ArrayElemNode(
    /**
     * Represent the array_elem expression
     * Examples: a[0], a[2][7], b[5], where `a` and `b` are arrays
     */
    /* the array where this array_elem is located */
    val array: ExprNode, /* a list of indices needed in multilevel indexing. e.g. a[3][4][5] */
    var index: List<ExprNode>,
    type: Type,
    val name: String,
    val symbol: Symbol
) :
    ExprNode() {
    init {
        this.type = type
        val arrayType = array.type as ArrayType
        if (arrayType.dimension < index.size) {
            indexOutOfBoundError(null, array.type as Type, index.size)
        }
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitArrayElemNode(this)
    }
}