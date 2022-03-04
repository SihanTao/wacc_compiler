import instruction.ARM11Instruction
import instruction.LABEL
import instruction.waccLibrary.WACCLibraryFunction
import java.io.PrintWriter
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap

class WACCCodeGenerator {
    private val staticDataTable = LinkedHashMap<String, Int>()
    private var dataElementCounter: Int = 0
    private val textSectionCode = LinkedList<ARM11Instruction>()
    private val codeDependencies = HashSet<WACCLibraryFunction>()


    fun addDataElement(data: String): Int {
        if (staticDataTable.containsKey(data)) {
            return staticDataTable.get(data)!!
        } else {
            staticDataTable.put(data, dataElementCounter)
            return dataElementCounter++
        }
    }

    fun addCode(instruction: ARM11Instruction) {
        textSectionCode.addLast(instruction)
    }

    fun addCodeDependency(function: WACCLibraryFunction) {
        codeDependencies.add(function)
        for (dependencies in function.getDependencies()) {
            addCodeDependency(dependencies)
        }
    }


    fun generateAssembleCode(writer: PrintWriter) {
        val library = LinkedList<ARM11Instruction>()
        codeDependencies.forEach { func -> library.addAll(func.getInstructions(this)) }

        if (!staticDataTable.isEmpty()) {
            writer.println(".data")
        }
        writer.println()
        staticDataTable.forEach{str, msgNo ->
            writer.println("msg_$msgNo:")
            writer.println("\t.word ${strLength(str)}")
            writer.println("\t.ascii \"$str\"")
        }
        writer.println()

        writer.println(".text")
        writer.println()
        textSectionCode.forEach{code -> writer.println(formatCode(code))}
        library.forEach{code -> writer.println(formatCode(code))}
    }

    private fun formatCode(instruction: ARM11Instruction): String {
        if (instruction is LABEL) {
            return instruction.toString()
        } else {
            return "\t" + instruction.toString()
        }
    }

    private fun strLength(str: String): Int {
        return str.length - str.count{ "\\".contains(it)}
    }

}
