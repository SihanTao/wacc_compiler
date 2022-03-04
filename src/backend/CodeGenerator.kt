package backend

import backend.directive.Code
import backend.directive.Data
import backend.directive.Directive
import backend.directive.Text

class CodeGenerator(data: Data?, text: Text?, code: Code?) {

    private val directives: MutableList<Directive?>

    fun generate(): String {
        val assemblyCodeBuilder = StringBuilder()
        for (directive in directives) {
            if (directive != null)
                assemblyCodeBuilder.append("$directive")
        }
        return assemblyCodeBuilder.toString()
    }

    init {
        directives = listOf(data, text, code).toMutableList()
    }
}