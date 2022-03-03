package symbolTable

import frontend.ErrorHandler
import node.expr.ExprNode

class SymbolTable(val parentSymbolTable: SymbolTable?) {
    /**
     * symbolTable.SymbolTable will record an identifier String and an ExprNode as the node representing the value
     * of that identifier in the current scope. It will also contain a copy of its parent symbolTable.SymbolTable.
     * The parent of the root symbolTable.SymbolTable will be set to null.
     */

    private val dictionary = HashMap<String, Symbol>()
    var tableSize = 0

    fun add(name: String, expr: ExprNode?, offset: Int): Boolean {
        if (dictionary.containsKey(name)) {
            ErrorHandler.symbolRedeclare(null, name)
            return true
        }

        dictionary[name] = Symbol(expr, offset)
        tableSize += expr!!.type!!.size()
        return false
    }

    fun add(name: String, expr: ExprNode?): Boolean {
        if (dictionary.containsKey(name)) {
            ErrorHandler.symbolRedeclare(null, name)
            return true
        }

        tableSize += expr!!.type!!.size()
        dictionary[name] = Symbol(expr, tableSize)

        return false
    }

    fun lookup(name: String): Symbol? {
        return dictionary[name]
    }

    fun lookupAll(name: String): Symbol? {
        var st: SymbolTable? = this
        var obj: Symbol? = null
        while (obj == null && st != null) {
            obj = st.lookup(name)
            st = st.parentSymbolTable
        }
        return obj
    }

    fun getStackOffset(name: String, symbol: Symbol): Int {
        if (dictionary.containsKey(name) && dictionary[name] == symbol) {
            return symbol.offset
        }

        if (parentSymbolTable != null) {
            return parentSymbolTable.getStackOffset(name, symbol) - parentSymbolTable.tableSize
        }

        println("symbolTable.SymbolTable getStackOffset Wrong")
        return -1
    }
}