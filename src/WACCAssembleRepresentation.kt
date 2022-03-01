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
    private var hasThrowErrorOverflow = false
    private var hasDivByZeroError = false
    private var hasCheckArrayBounds = false
    private var hasReadInt = false
    private var hasReadChar = false
    private var hasPrintRef = false

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

    fun addPrintIntFunc() {
        hasPrintInt = true
    }

    fun addPrintThrowErrorOverflowFunc() {
        hasThrowErrorOverflow = true
    }

    fun addPrintDivByZeroFunc() {
        hasDivByZeroError = true
    }

    fun addCheckErrorBoundsFunc() {
        hasCheckArrayBounds = true
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

    fun hasPrintThrowOverflowErrorFunc(): Boolean {
        return hasThrowErrorOverflow
    }

    fun hasPrintDivByZeroErrorFunc(): Boolean {
        return hasDivByZeroError
    }

    fun hasCheckArrayBoundsFunc(): Boolean {
        return hasCheckArrayBounds
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
        return msgTable.putIfAbsent(literal, Pair(msgTable.size, length))?.first ?: (msgTable.size -1)
    }

    fun addReadInt() {
        hasReadInt = true
    }

    fun hasReadIntFunc(): Boolean {
        return hasReadInt
    }

    fun hasReadCharFunc(): Boolean {
        return hasReadChar
    }

    fun addReadCharFunc() {
        hasReadChar = true
    }

    fun addPrintReference() {
        hasPrintRef = true
    }

    fun hasPrintRefFunction(): Boolean {
        return hasPrintRef
    }


}