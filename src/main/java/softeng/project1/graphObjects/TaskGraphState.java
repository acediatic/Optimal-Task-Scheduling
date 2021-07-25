package softeng.project1.graphObjects;

import java.util.Map;

public class TaskGraphState implements TaskGraph {

    private final TaskGraph originalTaskGraph;

    private final Map<String, TaskNode> changedTaskNodes;
    private final Map<String, TaskNode> freeTaskNodes;

    public TaskGraphState(TaskGraph previousState, TaskNode newTaskNode) {

        this.originalTaskGraph = previousState.getOriginalTaskGraph();
        this.changedTaskNodes = previousState.getTaskNodes();
    }

}
