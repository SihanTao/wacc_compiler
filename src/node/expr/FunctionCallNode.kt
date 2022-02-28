package node.expr

import node.FuncNode
import SymbolTable

class FunctionCallNode(val function: FuncNode, val params: List<ExprNode>, currScope: SymbolTable<ExprNode>?) :
    
    /**
     * Represent a function call with a list of parameters and a SymbolTable
     * Examples: boolean = call validChar('a');
     */

    ExprNode() {
    private val funcSymbolTable: SymbolTable<ExprNode>

    init {
        funcSymbolTable = SymbolTable(currScope)
        type = function.returnType
    }
}