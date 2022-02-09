import node.expr.ExprNode

class SymbolTable(parentSymbolTable: SymbolTable) {
    private var parentSymbolTable: SymbolTable

    /**
     * SymbolTable will record an identifier String and an ExprNode as the node representing the value
     * of that identifier in the current scope. It will also contain a copy of its parent SymbolTable.
     * The parent of the root SymbolTable will be set to null.
     */
    private val dictionary = java.util.HashMap<String, ExprNode>()

    init {
        this.parentSymbolTable = parentSymbolTable
    }

    fun add(name: String, expr: ExprNode): Boolean {
        // TODO: check whether symbol has been declared

        dictionary[name] = expr
        return false
    }

    fun lookup(name: String): ExprNode? {
        return dictionary[name]
    }

    fun lookupAll(name: String): ExprNode? {
        var st = this
        var obj: ExprNode? = null
        while (obj == null && st != null) {
            obj = st.lookup(name)
            st = st.parentSymbolTable
        }
        return obj
    }
}