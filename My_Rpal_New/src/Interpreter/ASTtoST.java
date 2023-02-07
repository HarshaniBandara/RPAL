
package Interpreter;

/**
 * class to convert ast to st.
 */
public class ASTtoST {
    /**
     * Error throwing function to check for the given number of children
     */
    private static void expectChildren(Node node, int expect) {
        if (!node.hasChildren(expect)) {
            String errorMessage = String.format("Expected %s node to have %s nodes", node.getLabel(), expect);
            throw new ExceptionHandlerOfAST(errorMessage);
        }
    }

    /**
     * Error throwing function to check for more than a given number of children
     */
    private static void expectMoreChildren(Node node, int minimum) {
        if (node.getNumChild() < minimum) {
            String errorMessage = String.format("Expected %s node to have at least %s nodes", node.getLabel(), minimum);
            throw new ExceptionHandlerOfAST(errorMessage);
        }
    }

    /**
     * Error throwing function to check for the given label.
     */
    private static void checkLabel(Node node, String expect) {
        if (!node.isLabel(expect)) {
            String errorMessage = String.format("Expected %s node but found %s node", expect, node.getLabel());
            throw new ExceptionHandlerOfAST(errorMessage);
        }
    }
    /**
     * Converts ast to st.
     * <p>
     * Not standardizing uop/op Nodes <br/>
     * Not standardizing -> Nodes <br/>
     * Not standardizing tau Nodes <br/>
     * Not standardizing , Nodes
     *
     * @param node Node of the subtree to standardize.
     */

    public static void astToSt(Node node) {
        // Ast -> st conversion
        node.forEachChild(ASTtoST::astToSt);

        if (node.isLabel("let")) {
            checkLabel(node, "let");
            expectChildren(node, 2);
            Node eqNode = node.getChild(0);
            Node pNode = node.getChild(1);

            checkLabel(eqNode, "=");
            expectChildren(eqNode, 2);
            Node xNode = eqNode.getChild(0);
            Node eNode = eqNode.getChild(1);

            // Reorganize tree
            node.setLabel("gamma");
            eqNode.setLabel("lambda");
            node.clearChildren();
            node.addChild(eqNode);
            node.addChild(eNode);
            eqNode.clearChildren();
            eqNode.addChild(xNode);
            eqNode.addChild(pNode);
        } else if (node.isLabel("where")) {
//            stForWhere(node);
            checkLabel(node, "where");
            expectChildren(node, 2);
            Node pNode = node.getChild(0);
            Node eqNode = node.getChild(1);

            checkLabel(eqNode, "=");
            expectChildren(eqNode, 2);
            Node xNode = eqNode.getChild(0);
            Node eNode = eqNode.getChild(1);

            // Reorganize tree
            node.setLabel("gamma");
            eqNode.setLabel("lambda");

            node.clearChildren();
            node.addChild(eqNode);
            node.addChild(eNode);
            eqNode.clearChildren();
            eqNode.addChild(xNode);
            eqNode.addChild(pNode);
        } else if (node.isLabel("function_form")) {

            checkLabel(node, "function_form");
            expectMoreChildren(node, 3);

            int numberOfVNodes = node.getNumChild() - 2;
            Node pNode = node.getChild(0);
            Node eNode = node.getChild(numberOfVNodes + 1);
            Node[] vNodes = new Node[numberOfVNodes];
            for (int i = 0; i < numberOfVNodes; i++) {
                vNodes[i] = node.getChild(i + 1);
            }

            // Reorganize tree
            node.setLabel("=");
            node.clearChildren();
            node.addChild(pNode);
            Node prevNode = node;
            for (int i = 0; i < numberOfVNodes; i++) {
                Node currentNode = new Node("lambda");
                prevNode.addChild(currentNode);
                currentNode.addChild(vNodes[i]);
                prevNode = currentNode;
            }
            prevNode.addChild(eNode);
        } else if (node.isLabel("and")) {
            checkLabel(node, "and");
            expectMoreChildren(node, 2);

            int numberOfEqNodes = node.getNumChild();
            Node[] eqNodes = new Node[numberOfEqNodes];
            for (int i = 0; i < numberOfEqNodes; i++) {
                eqNodes[i] = node.getChild(i);
                checkLabel(eqNodes[i], "=");
                expectChildren(eqNodes[i], 2);
            }

            // Reorganize tree
            node.setLabel("=");
            node.clearChildren();
            Node commaNode = new Node(",");
            Node tauNode = new Node("tau");
            node.addChild(commaNode);
            node.addChild(tauNode);
            for (int i = 0; i < numberOfEqNodes; i++) {
                Node xNode = eqNodes[i].getChild(0);
                Node eNode = eqNodes[i].getChild(1);
                commaNode.addChild(xNode);
                tauNode.addChild(eNode);
            }
        } else if (node.isLabel("rec")) {
            checkLabel(node, "rec");
            expectChildren(node, 1);
            Node eqNode = node.getChild(0);

            checkLabel(eqNode, "=");
            expectChildren(eqNode, 2);
            Node xNode = eqNode.getChild(0);
            Node eNode = eqNode.getChild(1);

            // Reorganize tree
            Node secondXNode = xNode.copy();
            node.setLabel("=");
            node.clearChildren();
            Node gammaNode = new Node("gamma");
            Node yStarNode = new Node("yStar");
            Node lambdaNode = new Node("lambda");
            node.addChild(xNode);
            node.addChild(gammaNode);
            gammaNode.addChild(yStarNode);
            gammaNode.addChild(lambdaNode);
            lambdaNode.addChild(secondXNode);
            lambdaNode.addChild(eNode);
        } else if (node.isLabel("lambda")) {
            checkLabel(node, "lambda");
            expectMoreChildren(node, 2);

            int numberOfVNodes = node.getNumChild() - 1;
            Node[] vNodes = new Node[numberOfVNodes];
            Node eNode = node.getChild(numberOfVNodes);
            for (int i = 0; i < numberOfVNodes; i++) {
                vNodes[i] = node.getChild(i);
            }

            // Reorganize tree
            Node currentLambdaNode = node;
            currentLambdaNode.clearChildren();
            currentLambdaNode.addChild(vNodes[0]);
            for (int i = 1; i < numberOfVNodes; i++) {
                Node newLambdaNode = new Node("lambda");
                currentLambdaNode.addChild(newLambdaNode);
                newLambdaNode.addChild(vNodes[i]);
                currentLambdaNode = newLambdaNode;
            }
            currentLambdaNode.addChild(eNode);
        } else if (node.isLabel("within")) {
            checkLabel(node, "within");
            expectChildren(node, 2);
            Node eq1Node = node.getChild(0);
            Node eq2Node = node.getChild(1);

            checkLabel(eq1Node, "=");
            expectChildren(eq1Node, 2);
            checkLabel(eq2Node, "=");
            expectChildren(eq2Node, 2);
            Node x1Node = eq1Node.getChild(0);
            Node e1Node = eq1Node.getChild(1);
            Node x2Node = eq2Node.getChild(0);
            Node e2Node = eq2Node.getChild(1);

            // Reorganize tree
            Node gammaNode = new Node("gamma");
            Node lambdaNode = new Node("lambda");
            node.setLabel("=");
            node.clearChildren();
            node.addChild(x2Node);
            node.addChild(gammaNode);
            gammaNode.addChild(lambdaNode);
            gammaNode.addChild(e1Node);
            lambdaNode.addChild(x1Node);
            lambdaNode.addChild(e2Node);
        } else if (node.isLabel("@")) {
            checkLabel(node, "@");
            expectChildren(node, 3);
            Node e1Node = node.getChild(0);
            Node nNode = node.getChild(1);
            Node e2Node = node.getChild(2);

            // Reorganize tree
            node.clearChildren();
            node.setLabel("gamma");
            Node gammaNode = new Node("gamma");
            node.addChild(gammaNode);
            node.addChild(e2Node);
            gammaNode.addChild(nNode);
            gammaNode.addChild(e1Node);
        }
    }




}
