package org.pcl.structure.tree;

import edu.uci.ics.jung.graph.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Create the syntax tree */
public class SyntaxTree {

    /** Root node of the syntax tree. */
    private final Node rootNode;

    public SyntaxTree(){ this.rootNode = null; }
    public SyntaxTree(Node rootNode) {
        this.rootNode = rootNode;
    }

    /** Return the root node of the syntax tree. */
    public Node getRootNode() {
        return this.rootNode;
    }

    /** Navigate the syntax tree. */
    public void navigate() {
        depthFirstNavigation(this.rootNode);
    }

    /** Navigate the syntax tree in depth first. */
    private void depthFirstNavigation(Node node) {
        if (node.isFinal()) {
            System.out.println(node.getToken());
        } else {
            for (Node child : node.getChildren()) {
                depthFirstNavigation(child);
            }
        }
    }

    public DirectedGraph<Node, Number> toGraph() {
        DirectedGraph<Node, Number> g =
                new DirectedOrderedSparseMultigraph<>();
        AtomicInteger i = new AtomicInteger(1);

        assert this.rootNode != null;
        ArrayList<Node> nodes = new ArrayList<>();

        //g.addVertex(rootNode.getValue());
        nodes.add(rootNode);
        for (Node node: rootNode.getChildren()) {
            if (!nodes.contains(node)) {
                nodes.add(node);
                //g.addVertex(node.getValue());
            }
        }
        for (Node node : this.rootNode.getChildren()) {
            i.set(i.get() + 1);
            System.out.println(node.getValue() + " number " + i.get());
            g.addEdge(i.get(), rootNode, node);
            addToGraph(nodes, node, g, i);
        }

        return g;
    }

    private void addToGraph(ArrayList<Node> nodes, Node node, Graph<Node, Number> g, AtomicInteger number) {
        for (Node child : node.getChildren()) {
            if (!nodes.contains(child)) {
                nodes.add(child);
                //g.addVertex(child.getValue());
            }

            number.set(number.get() + 1);
            g.addEdge(number.get(), node, child);
            addToGraph(nodes, child, g, number);
        }
    }


    /** Return the syntax tree as a string. */
    @Override
    public String toString() {
        return "SyntaxTree{" +
                "rootNode=" + rootNode +
                '}';
    }
}
