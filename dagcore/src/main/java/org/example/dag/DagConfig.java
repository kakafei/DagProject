package org.example.dag;

import java.util.List;

public class DagConfig {
    private List<NodeConfig> nodes;

    // 必须提供setter
    public void setNodes(List<NodeConfig> nodes) {
        this.nodes = nodes;
    }

    public List<NodeConfig> getNodes() {
        return nodes;
    }

    public static class NodeConfig {
        private String id;
        private String className;
        private List<String> dependencies;

        private long timeOut;


        // 只保留getters
        public String getId() { return id; }
        public String getClassName() { return className; }
        public List<String> getDependencies() { return dependencies; }

        public void setId(String id) {
            this.id = id;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }

        public long getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(long timeOut) {
            this.timeOut = timeOut;
        }
    }
}