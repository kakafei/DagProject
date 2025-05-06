package org.example.web;

import org.example.dag.*;

import java.io.InputStream;
import java.util.List;

/**
 * 1. dag中可以根据任务的具体耗时，进行动态分配线程池。防止长时间任务阻塞整体执行。
 */
public class DagRunner {
    public static void main(String[] args) throws Exception {
        // 1. 加载JSON配置
        InputStream configStream = DagRunner.class.getResourceAsStream("/dag.json");
        DagConfig config = DagParser.parseConfig(configStream);

        // 2. 创建DAG节点
        List<TaskNode> nodes = DagParser.createNodes(config);

        // 3. 初始化DAG引擎
        DagEngine engine = new DagEngine();
        nodes.forEach(engine::registerNode);

        // 4. 执行DAG
        DagContext context = new DagContext();
        context.setStart(System.currentTimeMillis());
        context.setTimeout(50);
        Object results = engine.execute(context);
        
        // 5. 输出最终结果
        System.out.println("\nFinal Results:");
        System.out.println(results);
    }
}