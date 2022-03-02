package node.expr

import node.FuncNode
import SymbolTable

class FunctionCallNode(private val function: FuncNode, private val params: List<ExprNode>, currScope: SymbolTable?) :
    
    /**
     * Represent a function call with a list of parameters and a SymbolTable
     * Examples: boolean = call validChar('a');
     */

    ExprNode() {
    private val funcSymbolTable: SymbolTable

    init {
        funcSymbolTable = SymbolTable(currScope)
        type = function.returnType
        weight = 1
    }
}