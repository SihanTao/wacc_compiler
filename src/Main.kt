// import ANTLR package
import antlr.*
import node.ProgramNode
import org.antlr.v4.runtime.*
import java.io.FileNotFoundException
import java.io.PrintWriter
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

        val filename = if (args.isEmpty()) null else
            args[0].substringAfterLast("/").substringBeforeLast(".")

        val lexer = WACCLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)

        parser.removeErrorListeners()
        parser.addErrorListener(WACCSyntaxErrorListener())

        val tree: WACCParser.ProgramContext = parser.program()

        WACCSyntaxErrorVisitor(parser).visit(tree)
        if (parser.numberOfSyntaxErrors > 0 ) {
            println(parser.numberOfSyntaxErrors.toString() + " syntax errors detected, "
              + " failing with exit code " + SYNTAX_ERROR_EXIT_CODE)
            exitProcess(SYNTAX_ERROR_EXIT_CODE)
        }

        if (!args.contains("--parse-only")) {
            val semanticChecker = WACCSemanticErrorVisitor()
            val ast = semanticChecker.visitProgram(tree) as ProgramNode
            val writer = if (filename == null) PrintWriter("output.s") else
                PrintWriter("$filename.s")

            val optimisation = args.find {i -> i.contains("-o")}
            if (optimisation != null) {
                val optimisationLevel = optimisation.substringAfter("-o").toInt()
                if (optimisationLevel in 1..5) {
                    val optimiser = WACCOptimiserVisitor(optimisationLevel)
                    optimiser.visitProgramNode(ast)
                }
                println("OPTIMISED")
            }

            val codeGenerator = WACCCodeGenerator()
            val codeGeneratorVisitor = WACCCodeGeneratorVisitor(codeGenerator)
            codeGeneratorVisitor.visitProgramNode(ast)
            codeGenerator.generateAssembleCode(writer)
            writer.close()
        }

        if (args.contains("--print_ast")) {
            println(tree.toStringTree(parser)) // Print LISP-style tree
        }
    } catch (e: FileNotFoundException) {
        println("FileNotFoundException in Main.kt: Cannot find the file")
    }
}
