package backend

class CodeGenerator(data: Data?, text: Text?, code: Code?) {

    private val directives: MutableList<Directive?>

    fun generate(): String {
        val assemblyCodeBuilder = StringBuilder()
        for (directive in directives) {
            if (directive != null)
                assemblyCodeBuilder.append(directive.toString())
        }
        return assemblyCodeBuilder.toString()
    }

    init {
        directives = listOf(data, text, code).toMutableList()
    }
}