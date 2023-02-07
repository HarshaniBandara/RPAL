package Interpreter;

/**
 * Exception when standardizing the abstract syntax tree.
 */
public class ExceptionHandlerOfAST extends RuntimeException {
    ExceptionHandlerOfAST(String message) {
        super(message);
    }
}
