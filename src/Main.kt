// import ANTLR package
import antlr.*
import errorHandler.WACCSyntaxErrorStrategy
import org.antlr.v4.runtime.*
import java.io.FileNotFoundException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        val input: CharStream = if (args.isEmpty()) {
            // Read from standard in if file not supplied
            CharStreams.fromStream(System.`in`)
        } else {
            val file = java.io.File(args[0])
            val fileInputStream =
                java.io.FileInputStream(file)
            CharStreams.fromStream(fileInputStream)
        }
        val lexer = WACCLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)
        parser.errorHandler = WACCSyntaxErrorStrategy()
        val tree: WACCParser.ProgramContext = parser.program()
        if (parser.numberOfSyntaxErrors > 0) {
            exitProcess(100)
        }

        if (!args.contains("--parse-only")) {
            val semanticChecker = WACCSemanticErrorVisitor()
            semanticChecker.visitProgram(tree)
        }

        if (args.contains("--print_ast")) {
            println(tree.toStringTree(parser)) // Print LISP-style tree
        }
    } catch (e: FileNotFoundException) {
        println("FileNotFoundException in Main.kt: Cannot find the file")
    }
}
