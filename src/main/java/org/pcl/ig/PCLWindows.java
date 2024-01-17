package org.pcl.ig;


import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.pcl.Token;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PCLWindows {


    private final ArrayList<Token> tokens;
    private final SyntaxTree syntaxTree;

    private boolean showTree;

    private DirectedGraph<Node,Number> graph;
    private Forest<Node,Number> tree;

    private Dimension preferredSizeRect = new Dimension(800,800);

    public PCLWindows(ArrayList<Token> tokens, SyntaxTree syntaxTree, boolean showTree) {
        this.tokens = tokens;
        this.syntaxTree = syntaxTree;
        this.showTree = showTree;
    }

    public void start() {
        JFrame frame = new JFrame("PCL AST");
        // Set the size of the windows
        frame.setSize(1200, 800);
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the windows open in the center of the screen
        frame.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        if (showTree) {
            setupTree(frame);
            frame.setBackground(Color.WHITE);
            frame.setResizable(false);
            frame.setVisible(true);
        }

        // Set the JFrame to be visible

    }

    public void setupTree(JFrame frame) {
        Container mainPanel = frame.getContentPane();
        JPanel centerPanel = new JPanel();


        graph = syntaxTree.toGraph();


        tree = new DelegateForest<Node, Number>(graph);

        Layout<Node,Number> layout1 = new OrderedTreeLayout<>(tree);
        VisualizationModel<Node,Number> vm1 =
                new DefaultVisualizationModel<>(layout1, preferredSizeRect);


        VisualizationViewer<Node,Number> vv = new VisualizationViewer<>(vm1, preferredSizeRect);


        vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(graph));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(new VertexColor());
        vv.getRenderContext().setVertexShapeTransformer(new ShapeLabelTransform());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.setForeground(Color.BLACK);
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.setAlignmentX(Component.CENTER_ALIGNMENT);


        centerPanel.setPreferredSize(new Dimension(1180, 800));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vv.setPreferredSize(new Dimension(1180, 800));

        DefaultModalGraphMouse<String, Number> gm1 = new DefaultModalGraphMouse<>();
        //AbstractModalGraphMouse gm1 = new ModalLensGraphMouse();
        vv.setGraphMouse(gm1);
        final ScalingControl scaler = new CrossoverScalingControl();
        vv.scaleToLayout(scaler);


        vv.setBackground(Color.WHITE);
        GraphZoomScrollPane graphZoomScrollPane = new GraphZoomScrollPane(vv);
        graphZoomScrollPane.setBackground(Color.WHITE);
        mainPanel.setBackground(Color.WHITE);
        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(graphZoomScrollPane);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
    }

}

