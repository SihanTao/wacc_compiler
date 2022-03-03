package node.expr

import node.FuncNode
import symbolTable.SymbolTable
import backend.ASTVisitor

class FunctionCallNode(
    val function: FuncNode,
    val params: List<ExprNode>,
    currScope: SymbolTable?
) : ExprNode() {
    private val funcSymbolTable: SymbolTable

    init {
        funcSymbolTable = SymbolTable(currScope)
        type = function.returnType
        weight = 1
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitFunctionCallNode(this)
    }
}