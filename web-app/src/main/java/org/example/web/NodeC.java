package org.example.web;

import org.example.dag.BaseTaskNode;
import org.example.dag.DagContext;

import java.util.List;

public class NodeC extends BaseTaskNode {
    public NodeC(String id, List<String> dependencies) {
        super(id, dependencies);
    }

    @Override
    public Object execute(DagContext context) {
        System.out.println("Processing Node C");
        List<Object> rs = getDependencyResults(context);
        return "C" + rs.toString();
    }
}