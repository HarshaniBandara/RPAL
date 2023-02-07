import Interpreter.ASTtoST;
import cse.ElementParser;
import cse.CSEMachine;
import cse.Stack;
import cse.ele.EleValue;

import Interpreter.CreateTree;
import Interpreter.Node;
import Interpreter.ExceptionHandlerOfAST;
import  cse.ExceptionHandlerOfCSE;
import java.util.ArrayList;

public class myrpal {
    String error;
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                String error="You shold give the AST file name as a argument...";
                throw new Exception(error);
            }
            else{
                String ASTFileName = args[0];
                Node root = CreateTree.nodeFromFile(ASTFileName);
                ASTtoST.astToSt(root);
                ArrayList<Stack<EleValue>> controls = ElementParser.generateCS(root);
                CSEMachine cseMachine = new CSEMachine(controls);
                cseMachine.evaluateTree();
            }

        }
        catch (ExceptionHandlerOfAST exception) {
            System.out.println("Error occurred while standardizing ast:");
            System.out.println(exception.getMessage());
        } catch (ExceptionHandlerOfCSE exception) {
            System.out.println("Error occurred while evaluating cse:");
            System.out.println(exception.getMessage());
        } catch (RuntimeException exception) {
            System.out.println("Runtime Exception:");
            System.out.println(exception.getMessage());
        }catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
