package node.stat

import SymbolTable
import node.Node

abstract class StatNode : Node {

    private var isReturned = false
    private var scope: SymbolTable? = null

    protected fun returned() {
        isReturned = true
    }

    protected fun notReturned() {
        isReturned = false
    }

    fun setScope(scope: SymbolTable?) {
        this.scope = scope
    }

    /* Getters */
    fun isReturned(): Boolean {
        return isReturned
    }

    fun asStatNode(): StatNode {
        return this
    }
}