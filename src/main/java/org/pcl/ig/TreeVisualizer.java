package org.pcl.ig;

import edu.uci.ics.jung.graph.DelegateTree;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

public class TreeVisualizer {

    public static DelegateTree<Node, Integer> createDisplayTree(SyntaxTree root) {
        DelegateTree<Node, Integer> tree = new DelegateTree<>();

        int edgeCount = 0;

        if (root != null) {
            tree.setRoot(root.getRootNode());
            createSubtree(tree, root.getRootNode(), edgeCount);
        }

        return tree;
    }


    private static void createSubtree(DelegateTree<Node, Integer> tree, Node node, int edgeCount) {
        for (Node child : node.getChildren()) {
            tree.addChild(edgeCount++, node, child);
            createSubtree(tree, child, edgeCount);
        }
    }


}