// import ANTLR package
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import antlr.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val input: CharStream = if (args.isEmpty()) {
        // Read from standard in if file not supplied
        CharStreams.fromStream(System.`in`)
    } else {
        val file = java.io.File(args[0])
        val fileInputStream = java.io.FileInputStream(file)
        CharStreams.fromStream(fileInputStream)
    }
    val lexer = WACCLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = WACCParser(tokens)
    val tree: ParseTree = parser.program()
    if (parser.getNumberOfSyntaxErrors() > 0) {
        exitProcess(100)
    }
    val visitor = MyVisitor()
    visitor.visit(tree)
}
