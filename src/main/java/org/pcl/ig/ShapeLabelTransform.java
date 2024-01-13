package org.pcl.ig;

import org.pcl.structure.tree.Node;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShapeLabelTransform implements com.google.common.base.Function<Object, java.awt.Shape> {
    @Override
    public Shape apply(Object input) {
        Node node = (Node) input;
        double width = node.getValue().length() * 10.0;
        return new Ellipse2D.Double(-(width/2), -12.5, width, 25);
    }

}
