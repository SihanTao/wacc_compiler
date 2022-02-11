// import ANTLR package
import antlr.*
import errorHandler.SyntaxErrorHandler
import org.antlr.v4.runtime.*
import java.io.FileNotFoundException

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
        parser.errorHandler = SyntaxErrorHandler()
        val tree: WACCParser.ProgramContext = parser.program()

        if (!args.contains("--parse-only")) {
            val semanticChecker = MyVisitor()
            semanticChecker.visitProgram(tree)
        }

        if (args.contains("--print_ast")) {
            println(tree.toStringTree(parser)) // Print LISP-style tree
        }
    } catch (e: FileNotFoundException) {
        println("FileNotFoundException in Main.kt: Cannot find the file")
    }
}
