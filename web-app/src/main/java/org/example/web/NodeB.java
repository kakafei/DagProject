package org.example.web;

import org.example.dag.BaseTaskNode;
import org.example.dag.DagContext;

import java.util.List;

public class NodeB extends BaseTaskNode {
    public NodeB(String id, List<String> dependencies) {
        super(id, dependencies);
    }

    @Override
    public Object execute(DagContext context) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Processing Node B");
        return "B";
    }
}