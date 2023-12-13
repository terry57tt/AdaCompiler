package org.pcl.ig;


import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.pcl.Token;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PClWindows {


    private ArrayList<Token> tokens;
    private SyntaxTree syntaxTree;

    public PClWindows(ArrayList<Token> tokens, SyntaxTree syntaxTree) {
        this.tokens = tokens;
        this.syntaxTree = syntaxTree;
    }

    public void start() {
        JFrame frame = new JFrame("PCL windows");
        // Set the size of the windows        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setSize(1200, 800);
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Make the windows open in the center of the screen
        frame.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        setupTokens(frame, tokens);
        setupTree(frame);

        frame.setResizable(false);
        // Set the JFrame to be visible
        frame.setVisible(true);
    }

    public void setupTree(JFrame frame) {
        Container mainPanel = frame.getContentPane();
        JPanel centerPanel = new JPanel();

        BasicVisualizationServer<Node, Integer> graphComponent = new BasicVisualizationServer<>(
                new TreeLayout<>(TreeVisualizer.createDisplayTree(syntaxTree)),
                new Dimension(800, 800)
        );

        graphComponent.getRenderContext().setEdgeLabelTransformer(new NodeLabeller());
        graphComponent.getRenderContext().setVertexLabelTransformer(new NodeLabeller());
        graphComponent.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        graphComponent.getRenderContext().setVertexFillPaintTransformer(new ColorApplier());


        //JScrollPane scrollPane = new JScrollPane(graphComponent);
        graphComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        centerPanel.setPreferredSize(new Dimension(800, 800));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphComponent.setPreferredSize(new Dimension(800, 800));
        //scrollPane.setPreferredSize(new Dimension(800, 800));
        centerPanel.add(graphComponent);
        //centerPanel.add(scrollPane);
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

