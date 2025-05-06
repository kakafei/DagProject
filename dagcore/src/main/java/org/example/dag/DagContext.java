package org.example.dag;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DagContext {
    private final Map<String, Object> results = new ConcurrentHashMap<>();

    private long start = System.currentTimeMillis();
    // 整体超时时间
    private long timeout = 1000;

    public void putResult(String nodeId, Object result) {
        results.put(nodeId, result);
    }

    public Object getResult(String nodeId) {
        return results.get(nodeId);
    }

    public Map<String, Object> getAllResults() {
        return new HashMap<>(results);
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
