package org.pcl.ig;

import com.google.common.base.Function;
import org.pcl.structure.tree.Node;

import java.awt.*;

public class VertexColor implements Function<Node, Paint> {
    @Override
    public Paint apply(Node input) {
        return (Paint) Color.WHITE;
    }
}
