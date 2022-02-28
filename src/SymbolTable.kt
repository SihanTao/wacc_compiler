import node.expr.ExprNode

class SymbolTable<T>(parentSymbolTable: SymbolTable<T>?) {
    var parentSymbolTable: SymbolTable<T>?

    /**
     * SymbolTable will record an identifier String and an ExprNode as the node representing the value
     * of that identifier in the current scope. It will also contain a copy of its parent SymbolTable.
     * The parent of the root SymbolTable will be set to null.
     */
    private val dictionary = java.util.HashMap<String, T>()

    init {
        this.parentSymbolTable = parentSymbolTable
    }

    fun add(name: String, expr: T?): Boolean {
        if (dictionary.containsKey(name)) {
            ErrorHandler.symbolRedeclare(null, name)
            Thread.dumpStack()
            return true
        }
        if (expr != null) {
            dictionary[name] = expr
        }
        return false
    }

    fun lookup(name: String): T? {
        return dictionary[name]
    }

    fun lookupAll(name: String): T? {
        var st: SymbolTable<T>? = this
        var obj: T? = null
        while (obj == null && st != null) {
            obj = st.lookup(name)
            st = st.parentSymbolTable
        }
        return obj
    }


}