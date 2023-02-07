package Interpreter;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Create tree from the input file
 */
public class CreateTree {

    public static Node nodeFromFile(String fileName) throws FileNotFoundException {
        List<String> lines = new LinkedList<>();
        File file = new File(fileName);
        /*
         *read file untill all the lines are read
         * if there are blank lines
         * finished the reading
         * if file name not found throws to the exception
         */
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            lines.add(data);
        }
        scanner.close();

        /**
         *create the node using the read lines of the AST
         */
        depthOfNode root = null;
        depthOfNode parent = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            //remove the "." in the begening of the line and get other part
            String data ="";
            for (int i = 0; i < line.length(); ++i) {
                char c = line.charAt(i);
                if (c != '.') {
//                    System.out.println(c);
                    data=line.substring(i);
                    break;
                }
            }

            int currentDepth = line.length() - data.length();
            if (currentDepth == 0 && root != null) break;
            while (parent != null && parent.getDepth() >= currentDepth) {
                parent = parent.getParent();
            }

            depthOfNode node;
            if (data.startsWith("<") && data.endsWith(">")) {
                // ID/EleValue nodes
                String label, value;
                if (data.contains(":")) {
                    // Str, Int, ... value nodes
                    int borderPos = data.indexOf(':');
                    label = data.substring(1, borderPos).toLowerCase();
                    if (label.equals("str")) {
                        // Str nodes: Remove quotations
                        value = data.substring(borderPos + 2, data.length() - 2);
                        // Evaluate string with \n,\t unescaped.
                        value = getJavaValue(value);
                    } else {
                        // Int nodes
                        value = data.substring(borderPos + 1, data.length() - 1);
                    }
                } else {
                    // Truth, value nodes
                    label = data.substring(1, data.length() - 1).toLowerCase();
                    value = null;
                }
                node = new depthOfNode(parent, label, value, currentDepth);
            } else {
                // Other nodes
                node = new depthOfNode(parent, data, currentDepth);
            }
            if (parent == null) {
                root = node;
            }
            parent = node;
        }
        return root;
    }
    /**
     * Snippet Taken From:
     * https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-java?answertab=votes#tab-top
     */
    private static String getJavaValue(String st) {
        StringBuilder stringBuilder = new StringBuilder(st.length());

        for (int ele = 0; ele < st.length(); ele++) {
            char character = st.charAt(ele);
            if (character == '\\') {
                char nextCharacter = (ele == st.length() - 1) ? '\\' : st
                        .charAt(ele + 1);
                // Octal escape?
                if (nextCharacter >= '0' && nextCharacter <= '7') {
                    String code = "" + nextCharacter;
                    ele++;
                    if ((ele < st.length() - 1) && st.charAt(ele + 1) >= '0'
                            && st.charAt(ele + 1) <= '7') {
                        code += st.charAt(ele + 1);
                        ele++;
                        if ((ele < st.length() - 1) && st.charAt(ele + 1) >= '0'
                                && st.charAt(ele + 1) <= '7') {
                            code += st.charAt(ele + 1);
                            ele++;
                        }
                    }
                    stringBuilder.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextCharacter) {
                    case '\\':
                        character = '\\';
                        break;
                    case 'b':
                        character = '\b';
                        break;
                    case 'f':
                        character = '\f';
                        break;
                    case 'n':
                        character = '\n';
                        break;
                    case 'r':
                        character = '\r';
                        break;
                    case 't':
                        character = '\t';
                        break;
                    case '\"':
                        character = '\"';
                        break;
                    case '\'':
                        character = '\'';
                        break;
                    case 'u':
                        if (ele >= st.length() - 5) {
                            character = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(ele + 2) + st.charAt(ele + 3)
                                        + st.charAt(ele + 4) + st.charAt(ele + 5), 16);
                        stringBuilder.append(Character.toChars(code));
                        ele += 5;
                        continue;
                }
                ele++;
            }
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }
}
