package org.example.web;

import org.example.dag.BaseTaskNode;
import org.example.dag.DagContext;

import java.util.List;

public class NodeD extends BaseTaskNode {
    public NodeD(String id, List<String> dependencies) {
        super(id, dependencies);
    }

    @Override
    public Object execute(DagContext context) {
        System.out.println("Processing Node D");

        return "D" + getDependencyResults(context).toString();
    }
}