package org.example.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 抽象任务节点（可选基础实现）
public class BaseTaskNode implements TaskNode {
    private final String id;

    private final List<String> dependencies;

    private long timeOut;

    public BaseTaskNode(String id, List<String> dependencies) {
        this.id = id;
        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        this.dependencies = dependencies;
    }

    @Override
    public Object execute(DagContext context) throws Exception {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

    protected List<Object> getDependencyResults(DagContext context) {
        if (dependencies == null || dependencies.isEmpty()) {
            return Collections.emptyList(); // 返回不可变空集合
        }

        List<Object> results = new ArrayList<>(dependencies.size());
        for (String depId : dependencies) {
            Object result = context != null ? context.getResult(depId) : null;
            results.add(result);
        }
        return results;
    }

    @Override
    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
