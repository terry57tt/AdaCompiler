import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.pcl.structure.tree.NodeType;
import org.pcl.structure.tree.Node;

public class SyntaxTreeTest {

    @Test
    public void createSyntaxTree() throws IOException {
        Node node = new Node(NodeType.PROGRAM, new ArrayList<>());
        assert node.getChildren().isEmpty();
        assert !node.isFinal();
        assert node.getToken() == null;
        assert node.getType() == NodeType.PROGRAM;
    }

}
