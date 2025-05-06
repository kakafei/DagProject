package org.example.dag;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DagEngine {
    private final Map<String, TaskNode> nodes = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 注册节点
    public void registerNode(TaskNode node) {
        nodes.put(node.getId(), node);
    }

    // 执行DAG
    public Object execute(DagContext context) throws InterruptedException {
        Map<String, List<String>> adjacencyList = buildAdjacencyList();
        Map<String, CompletableFuture<Void>> futures = new ConcurrentHashMap<>();

        // 拓扑排序
        List<String> executionOrder = topologicalSort(adjacencyList);
        if (executionOrder == null) throw new RuntimeException("Cycle detected in DAG");

        // 为每个节点创建CompletableFuture
        // 记录整体开始时间
        executionOrder.forEach(nodeId -> {
            TaskNode node = nodes.get(nodeId);

            // 获取依赖节点的future（可能为空）
            List<CompletableFuture<?>> dependencies = Collections.EMPTY_LIST;
            if (node.getDependencies() != null) {
                dependencies = node.getDependencies().stream()
                        .map(depId -> futures.get(depId))
                        .collect(Collectors.toList());
            }

            // 创建节点future
            CompletableFuture<Void> future;
            if (dependencies.isEmpty()) {
                // 入度为0的节点直接执行
                future = CompletableFuture.runAsync(() -> executeNode(node, context), executor);
            } else {
                // 有依赖的节点等待依赖完成
                future = CompletableFuture.allOf(dependencies.toArray(new CompletableFuture[0]))
                        .thenRunAsync(() -> executeNode(node, context), executor);
            }
            if (node.getTimeOut() > 0 || context.getTimeout() > 0) {
                future = future
                        .orTimeout(getTimeOut(node.getTimeOut(), context.getStart(), context.getTimeout()), TimeUnit.MILLISECONDS)
                        .exceptionally(ex -> {
                            if (ex instanceof TimeoutException) {
                                if (node.getDefaultValue() != null) {
                                    context.putResult(node.getId(), node.getDefaultValue());
                                }
                                System.out.println("Node " + node.getId() + " timed out, using default value");
                            }
                            return null;
                        });
            }

            futures.put(nodeId, future);
        });

        // 等待所有任务完成
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();
        return context.getResult(executionOrder.get(executionOrder.size()-1));
    }
    // 如果当前时间已经超过整体时间，则直接返回默认值
    private long getTimeOut(long timeout, long start, long allTimeOut) {
        if (timeout == 0) {
            timeout = Integer.MAX_VALUE;
        }
        return Math.min(timeout, allTimeOut + start - System.currentTimeMillis());
    }

    // 提取节点执行逻辑
    private void executeNode(TaskNode node, DagContext context) {
        try {
            Object result = node.execute(context);
            if (result != null) {
                context.putResult(node.getId(), result);
            }
        } catch (Exception e) {
            throw new CompletionException("Node execution failed: " + node.getId(), e);
        }
    }

    // 构建邻接表
    private Map<String, List<String>> buildAdjacencyList() {
        Map<String, List<String>> adjacencyList = new HashMap<>();
        nodes.values().forEach(node -> {
            // 安全获取依赖列表，处理null和不可变集合
            List<String> dependencies = Optional.ofNullable(node.getDependencies())
                    .map(ArrayList::new)  // 创建新集合防止不可变列表
                    .orElseGet(ArrayList::new);

            adjacencyList.put(node.getId(), dependencies);
        });
        return adjacencyList;
    }

    // 拓扑排序（Kahn算法）
    private List<String> topologicalSort(Map<String, List<String>> adjacencyList) {
        // 修正入度计算和邻接表方向
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> reversedAdjacency = new HashMap<>(); // 新增反向邻接表

        // 初始化数据结构
        nodes.keySet().forEach(node -> {
            inDegree.put(node, 0);
            reversedAdjacency.put(node, new ArrayList<>());
        });

        // 正确构建邻接关系：A -> B 表示 B 依赖 A
        adjacencyList.forEach((node, dependencies) -> {
            dependencies.forEach(dep -> {
                reversedAdjacency.get(dep).add(node); // 反转依赖方向
                inDegree.put(node, inDegree.get(node) + 1); // 正确计算入度
            });
        });

        Queue<String> queue = new LinkedList<>();
        List<String> result = new ArrayList<>();

        // 初始入度为0的节点
        inDegree.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .forEach(entry -> queue.add(entry.getKey()));

        // 处理队列
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current); // 正确的执行顺序

            // 处理当前节点的后继节点
            reversedAdjacency.get(current).forEach(successor -> {
                int newDegree = inDegree.get(successor) - 1;
                inDegree.put(successor, newDegree);

                if (newDegree == 0) {
                    queue.add(successor);
                }
            });
        }

        return result.size() == nodes.size() ? result : null;
    }
}