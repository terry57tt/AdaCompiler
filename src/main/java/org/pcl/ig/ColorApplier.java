package org.pcl.ig;

import org.pcl.structure.tree.Node;
import com.google.common.base.Function;

import java.awt.*;

public class ColorApplier implements Function<Node, Paint> {
    @Override
    public Paint apply(Node node) {
        return Color.cyan;
    }
}
