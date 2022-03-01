import node.expr.ExprNode

class SymbolTable(val parentSymbolTable: SymbolTable?) {
    /**
     * SymbolTable will record an identifier String and an ExprNode as the node representing the value
     * of that identifier in the current scope. It will also contain a copy of its parent SymbolTable.
     * The parent of the root SymbolTable will be set to null.
     */

    private val dictionary = HashMap<String, Symbol>()
    var tableSize = 0

    fun add(name: String, expr: ExprNode?): Boolean {
        if (dictionary.containsKey(name)) {
            ErrorHandler.symbolRedeclare(null, name)
            return true
        }

        if (expr != null) {
            tableSize += expr.type!!.size()
            dictionary[name] = Symbol(expr, tableSize)
        }

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
        return symbol.offset
    }
}