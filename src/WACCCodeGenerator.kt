import instruction.ARM11Instruction
import instruction.waccLibrary.WACCLibraryFunction
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDependencies

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

}
