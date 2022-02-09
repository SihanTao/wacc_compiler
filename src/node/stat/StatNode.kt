package node.stat

import SymbolTable
import node.Node

abstract class StatNode : Node {

    private var leaveAtEnd = false
    private var scope: SymbolTable? = null

    /* Set leaveAtEnd if needs overwrite */
    protected fun setLeaveAtEnd(value: Boolean) {
        leaveAtEnd = value
    }

    fun setScope(scope: SymbolTable?) {
        this.scope = scope
    }

    /* Getters */
    fun leaveAtEnd(): Boolean {
        return leaveAtEnd
    }

    fun asStatNode(): StatNode {
        return this
    }
}