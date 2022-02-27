// import ANTLR package
import antlr.*
import backend.Code
import backend.CodeGenerator
import backend.Data
import backend.Text
import backend.instructionGenerator.InstructionGenerator
import org.antlr.v4.runtime.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import kotlin.system.exitProcess

private const val SYNTAX_ERROR_EXIT_CODE = 100
private const val SEMANTIC_ERROR_EXIT_CODE = 200

fun main(args: Array<String>) {
    try {
        val file = File(args[0])
        val fileInputStream =
            java.io.FileInputStream(file)
        val input = CharStreams.fromStream(fileInputStream)

        val lexer = WACCLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)

        parser.removeErrorListeners()
        parser.addErrorListener(WACCSyntaxErrorListener())

        val tree: WACCParser.ProgramContext = parser.program()

        WACCSyntaxErrorVisitor(parser).visit(tree)

        if (parser.numberOfSyntaxErrors > 0) {
            exitProcess(SYNTAX_ERROR_EXIT_CODE)
        }


        if (!args.contains("--parse-only")) {
            val semanticChecker = WACCSemanticErrorVisitor()
            val ast = semanticChecker.visitProgram(tree)

            if (args.contains("--print_ast")) {
                println(tree.toStringTree(parser)) // Print LISP-style tree
            }

            if (args.contains("--assembly") || args.contains("--execute")) {
                // In this case, we need the assembly code
                val instructionGenerator = InstructionGenerator()
                instructionGenerator.visit(ast)
                val data = Data(instructionGenerator.dataSegment)
                val text = Text()
                val code = Code(instructionGenerator.instructions)
                val codeGenerator = CodeGenerator(data, text, code)

                // To write the code into a .s file
                val assemblyFile = File(file.name.replaceFirst(Regex("[.][^.]+$"), ".s"))
                println("Assembly file created successfully!")
                val fileWriter = FileWriter(assemblyFile)
                fileWriter.write(codeGenerator.generate())
                fileWriter.close()
                println("Write to assembly file successfully!")
            }
//            val writer = PrintWriter("output.s")
//            val representation = WACCAssembleRepresentation()
//            val codeGenerator = WACCCodeGeneratorVisitor(representation)
//            codeGenerator.visitProgramNode(ast)
//            representation.generateAssembleCode(writer)
//            writer.close()
        }


    } catch (e: FileNotFoundException) {
        println("FileNotFoundException in Main.kt: Cannot find the file")
    }
}
