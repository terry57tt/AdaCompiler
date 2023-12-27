package org.pcl.ig;


import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.pcl.Token;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PClWindows {


    private final ArrayList<Token> tokens;
    private final SyntaxTree syntaxTree;

    private DirectedGraph<Node,Number> graph;
    private Forest<Node,Number> tree;

    private Dimension preferredSizeRect = new Dimension(800,800);

    public PClWindows(ArrayList<Token> tokens, SyntaxTree syntaxTree) {
        this.tokens = tokens;
        this.syntaxTree = syntaxTree;
    }

    public void start() {
        JFrame frame = new JFrame("PCL windows");
        // Set the size of the windows
        frame.setSize(1200, 800);
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the windows open in the center of the screen
        frame.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        setupTokens(frame, tokens);
        setupTree(frame);
        frame.setBackground(Color.WHITE);

        frame.setResizable(false);
        // Set the JFrame to be visible
        frame.setVisible(true);
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


        centerPanel.setPreferredSize(new Dimension(800, 800));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vv.setPreferredSize(new Dimension(1200, 800));

        DefaultModalGraphMouse<String, Number> gm1 = new DefaultModalGraphMouse<>();
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


    public void setupTokens(JFrame frame, ArrayList<Token> tokens) {
        Container mainPanel = frame.getContentPane();

        frame.setBackground(Color.WHITE);
        JPanel rightPanel = new JPanel();
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightPanel.setPreferredSize(new Dimension(400, 800));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        for (Token token: tokens) {
            textArea.append(token.toString() + "\n");
        }
        textArea.setPreferredSize(new Dimension(400, 800));
        scrollPane.setPreferredSize(new Dimension(400, 800));
        rightPanel.add(scrollPane);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        frame.setContentPane(mainPanel);
    }


}

