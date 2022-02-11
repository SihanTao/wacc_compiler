package node.expr

import node.FuncNode
import SymbolTable

class FunctionCallNode(private val function: FuncNode, private val params: List<ExprNode>, currScope: SymbolTable?) :
    ExprNode() {
    private val funcSymbolTable: SymbolTable

    init {
        funcSymbolTable = SymbolTable(currScope)
        type = function.returnType
    }
}