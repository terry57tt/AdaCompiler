package org.pcl.structure.codeGeneration;

import org.pcl.structure.tds.Tds;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

public class CodeGenerator {
    SyntaxTree ast;
    Tds tds;

    public CodeGenerator(SyntaxTree ast, Tds tds) {
        if (ast == null || tds == null) {
            throw new IllegalArgumentException("ast et tds ne doivent pas être null");
        }

        this.ast = ast;
        this.tds = tds;

        generateCode(ast.getRootNode());
    }

    public void generateCode(Node node) {
        if (node == null) {
            return;
        }
        if(node.getType() != null) {
            switch (node.getType()) {
                case PROGRAM:
                    break;
                case IDENTIFIER:
                    break;
                case FILE:
                    break;
                case BODY:
                    break;
                case VIRGULE:
                    break;
                case TYPE:
                    break;
                case IS:
                    break;
                case ACCESS:
                    break;
                case RECORD:
                    break;
                case DECL_VAR:
                    break;
                case DECL_PROC:
                    break;
                case DECL_FUNC:
                    break;
                case FIELD:
                    break;
                case INITIALIZATION:
                    break;
                case PARAMETERS:
                    break;
                case MULTIPLE_PARAM:
                    break;
                case MODE:
                    break;
                case IN:
                    break;
                case INOUT:
                    break;
                case OR:
                    break;
                case ELSE:
                    break;
                case AND:
                    break;
                case THEN:
                    break;
                case NOT:
                    break;
                case SLASH_EQUAL:
                    break;
                case EQUAL:
                    break;
                case INFERIOR:
                    break;
                case INFERIOR_EQUAL:
                    break;
                case SUPERIOR:
                    break;
                case SUPERIOR_EQUAL:
                    break;
                case ADDITION:
                    break;
                case SUBSTRACTION:
                    break;
                case MULTIPLY:
                    break;
                case DIVIDE:
                    break;
                case REM:
                    break;
                case NEGATIVE_SIGN:
                    break;
                case POINT:
                    break;
                case INTEGER:
                    break;
                case CHARACTER:
                    break;
                case TRUE:
                    break;
                case FALSE:
                    break;
                case NULL:
                    break;
                case NEW:
                    break;
                case CHAR_VAL:
                    break;
                case RETURN:
                    break;
                case BEGIN:
                    break;
                case IF:
                    break;
                case FOR:
                    break;
                case WHILE:
                    break;
                case AFFECTATION:
                    break;
                case REVERSE:
                    break;
                case ELSIF:
                    break;
                case EXPRESSION:
                    break;
                case DECLARATION:
                    break;
                case COMPARATOR:
                    break;
                case CALL:
                    break;
                default:
                    throw new IllegalArgumentException("NodeType inconnu : " + node.getType());
            }
        }

        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                generateCode(child);
            }
        }
    }
}
