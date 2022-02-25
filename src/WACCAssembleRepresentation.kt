import java.io.PrintWriter
import java.util.*
import kotlin.collections.HashMap

class WACCAssembleRepresentation {
    private val code = LinkedList<String>()
    // Mapping from string to (no, length)
    val msgTable = HashMap<String, Pair<Int, Int>>()

    private var hasPrintString = false
    private var hasPrintIn = false
    private var hasPrintInt = false
    private var hasPrintBool = false

    fun addCode(str: String) {
        code.addLast(str)
    }

    fun addPrintStringFunc() {
        hasPrintString = true
    }

    fun addPrintInFunc() {
        hasPrintIn = true
    }

    fun addPrintBoolFunc() {
        hasPrintBool = true
    }

    fun hasPrintStringFunc(): Boolean {
        return hasPrintString
    }

    fun hasPrintInFunc(): Boolean {
        return hasPrintIn
    }

    fun hasPrintBoolFunc(): Boolean {
        return hasPrintBool
    }

    fun hasPrintIntFunc(): Boolean {
        return hasPrintInt
    }

    fun addPrintIntFunc() {
        hasPrintInt = true
    }

    fun generateAssembleCode(writer: PrintWriter) {
        if (!msgTable.isEmpty()) {
            writer.println(".data")
        }
        writer.println()
        msgTable.forEach{str, (no, len) ->
            writer.println("msg_$no:")
            writer.println("\t.word $len")
            writer.println("\t.ascii $str")
        }
        writer.println()
        writer.println(".text")
        code.forEach{code -> writer.println(code)}

    }

    fun addStringToTable(literal: String, length: Int): Int {
        return (msgTable.putIfAbsent(literal, Pair(msgTable.size, length))?.first ?: msgTable.size) -1
    }



}