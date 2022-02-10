package node.stat

import SymbolTable
import node.Node

abstract class StatNode : Node {

    var isReturned = false

    var scope: SymbolTable? = null

}