package org.pcl.ig;

import com.google.common.base.Function;

public class NodeLabeller implements Function<Object, String> {

    public String apply(Object o) {
       if (o instanceof Integer) return "";
       return o.toString();
    }
}