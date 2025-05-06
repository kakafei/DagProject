package org.example.dag;

import java.util.List;

public interface TaskNode {
    Object execute(DagContext context) throws Exception;

    String getId();

    List<String> getDependencies();

    /**
     * 超时时间，0默认不超时。
     * @return
     */
    default long getTimeOut(){
        return 0;
    }

    default Object getDefaultValue(){
        return null;
    }
}