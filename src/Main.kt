// import ANTLR package
import antlr.*
import org.antlr.v4.runtime.*
import java.io.FileNotFoundException
import kotlin.system.exitProcess

private const val SYNTAX_ERROR_EXIT_CODE = 100
private const val SEMANTIC_ERROR_EXIT_CODE = 200

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

//        parser.errorHandler = WACCSyntaxErrorStrategy()

        parser.removeErrorListeners()
        parser.addErrorListener(WACCSyntaxErrorListener())

        val tree: WACCParser.ProgramContext = parser.program()

        if (parser.numberOfSyntaxErrors > 0 ) {
//            println(parser.numberOfSyntaxErrors.toString() + " syntax errors detected, "
//              + " failing with exit code " + SYNTAX_ERROR_EXIT_CODE)
            exitProcess(SYNTAX_ERROR_EXIT_CODE)
        }

        WACCSyntaxErrorVisitor(parser).visit(tree)

        if (parser.numberOfSyntaxErrors > 0 ) {
//            println(parser.numberOfSyntaxErrors.toString() + " syntax errors detected, "
//              + " failing with exit code " + SYNTAX_ERROR_EXIT_CODE)
            exitProcess(SYNTAX_ERROR_EXIT_CODE)
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
