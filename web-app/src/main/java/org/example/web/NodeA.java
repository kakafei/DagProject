package org.example.web;

import org.example.dag.BaseTaskNode;
import org.example.dag.DagContext;

import java.util.List;

public class NodeA extends BaseTaskNode {
    public NodeA(String id, List<String> dependencies) {
        super(id, dependencies);
    }

    @Override
    public Object execute(DagContext context) {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Processing Node A");
        return "A";
    }
}