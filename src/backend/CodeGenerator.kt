package backend

import java.util.ArrayList
import java.util.List

class CodeGenerator(data: Data?, text: Text?, code: Code?) {
    private val directives: List<Directive>

    init {
        directives = List.of(data, text, code)
    }

    fun generate(): String {
        val assemblyCodeBuilder = StringBuilder()
        for (directive in directives) {
            assemblyCodeBuilder.append(directive.toString())
        }
        return assemblyCodeBuilder.toString()
    }
}