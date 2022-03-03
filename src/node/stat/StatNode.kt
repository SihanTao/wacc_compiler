package node.stat

import symbolTable.SymbolTable
import node.Node

    /**
     * abstract class for statement nodes. Any statement does not have a type
     */

abstract class StatNode : Node {

    var isReturned = false
    var scope: SymbolTable? = null

}