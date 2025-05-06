package org.example.dag;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class DagParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static DagConfig parseConfig(InputStream inputStream) throws Exception {
        return mapper.readValue(inputStream, DagConfig.class);
    }

    public static List<TaskNode> createNodes(DagConfig config) throws Exception {
        List<TaskNode> nodes = new ArrayList<>();
        for (DagConfig.NodeConfig nodeConfig : config.getNodes()) {
            Class<?>[] paramTypes = {String.class, List.class};
            Object[] params = {nodeConfig.getId(), nodeConfig.getDependencies()};
            BaseTaskNode node = (BaseTaskNode) createInstance(nodeConfig.getClassName(),paramTypes,params);
            node.setTimeOut(nodeConfig.getTimeOut());
            nodes.add(node);
        }
        return nodes;
    }
    public static Object createInstance(String className, Class<?>[] paramTypes, Object[] params)
            throws Exception {
        // 1. 加载类
        Class<?> clazz = Class.forName(className);

        // 2. 获取构造函数（包括私有构造）
        Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);

        // 3. 解除私有构造函数访问限制
        constructor.setAccessible(true);

        // 4. 传入参数创建实例
        return constructor.newInstance(params);
    }
}