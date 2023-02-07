package Interpreter;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Node {
    /**
     * Represents a node in the ast and st.
     *main two types they are child and parent
     * Label represents the type of Node.
     * EleValue represents the value of node
     */
    private final ArrayList<Node> children;
    private Node parent;
    private String label;
    private String value;
    /**
     *node with one argument called label
     */
    public Node(String label) {
        this.label = label;
        this.children = new ArrayList<>();
    }

    /**
     * node with two argument both lable and value
     */
    public Node(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }
    Node copy() {
        Node copied = new Node(label, value);
        for (int i = 0; i < getNumChild(); i++) {
            copied.addChild(getChild(i).copy());
        }
        return copied;
    }
    //get parent node
    Node getParent() {return parent;    }
    public String getLabel() {
        return label;
    }
    public String getValue() {
        return value;
    }
    public int getNumChild() {
        return children.size();
    }
    boolean hasChildren(int n) {
        return children.size() == n;
    }
    public boolean isLabel(String label) {
        return getLabel().equals(label);
    }
    public Node getChild(int i) {
        return children.get(i);
    }
    public void forEachChild(Consumer<? super Node> action) {
        children.forEach(action);
    }
    void setLabel(String label) {this.label = label;this.value = null;    }
    void clearChildren() {children.forEach(child -> child.parent = null);children.clear();}
    void addChild(Node child) {children.add(child);child.parent = this;}
}