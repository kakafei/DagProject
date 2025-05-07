package org.example.dag;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.*;

public class DagParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static DagConfig parseConfig(InputStream inputStream) throws Exception {
        return mapper.readValue(inputStream, DagConfig.class);
    }

    public static List<TaskNode> createNodes(DagConfig config) throws Throwable {
        List<TaskNode> nodes = new ArrayList<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (DagConfig.NodeConfig nodeConfig : config.getNodes()) {

            Class<?> clazz = Class.forName(nodeConfig.getClassName());
            Constructor<?> ctor = clazz.getDeclaredConstructor(String.class, List.class);
            ctor.setAccessible(true);
            MethodHandle methodHandle = lookup.unreflectConstructor(ctor);
            Object[] args = prepareArguments(nodeConfig);
            BaseTaskNode node = (BaseTaskNode) methodHandle.invokeWithArguments(args);
            node.setTimeOut(nodeConfig.getTimeOut());
            nodes.add(node);
        }
        return nodes;
    }

    private static Object[] prepareArguments(DagConfig.NodeConfig config) {
        return new Object[] {
                Objects.requireNonNull(config.getId(), "Node ID cannot be null"),
                Optional.ofNullable(config.getDependencies()).orElse(Collections.emptyList())
        };
    }

}