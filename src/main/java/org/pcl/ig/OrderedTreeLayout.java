package org.pcl.ig;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderedTreeLayout<V, E> extends TreeLayout<V, E> {


    public OrderedTreeLayout(Forest<V, E> g) {
        super(g);
    }



}