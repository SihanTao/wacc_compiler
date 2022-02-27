package backend.directives

import backend.Directive
import backend.instructions.Label
import java.lang.StringBuilder

class Data(private val messages: Map<Label, String>) : Directive {

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\t.data\n\n")
        for ((key, value) in messages) {
            sb.append("\t $key").append("\t\tword ${value.length}\n")
                .append("\t\t.ascii $value")
        }
        return sb.toString()
    }
}