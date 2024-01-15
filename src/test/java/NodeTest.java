import org.junit.jupiter.api.Test;
import org.pcl.Token;
import org.pcl.structure.tree.Node;

import java.util.ArrayList;

public class NodeTest {

    @Test
    public void nonTerminalInChildrenTest(){
        Token token = new Token("2");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Node root = new Node("Fichier");
        Node node1 = new Node("node1");
        Node node2 = new Node("node2");
        Node leaf = new Node(token);
        root.addChild(node1);
        node1.setParent(root);
        node1.addChild(node2);
        node2.setParent(node1);
        node2.addChild(leaf);
        leaf.setParent(node2);
        assert root.nonTerminalInChildren().equals(true);
        assert node1.nonTerminalInChildren().equals(true);
        assert node2.nonTerminalInChildren().equals(true);
        assert leaf.nonTerminalInChildren().equals(false);
    }
}
