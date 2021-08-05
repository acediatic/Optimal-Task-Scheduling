package softeng.project1.graph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class OriginalSchedule implements TaskGraph {

    private final Map<String, OriginalTaskNode> taskNodes;
    private final Map<String, OriginalTaskNode> freeTaskNodes;

    /**
     * TODO...
     */
    public OriginalSchedule(Map<String, OriginalTaskNode> taskNodes, Map<String, OriginalTaskNode> freeTaskNodes) {
        // Making these immutable, note that the underlying map can still be changed.
        this.taskNodes = Collections.unmodifiableMap(taskNodes);
        this.freeTaskNodes = Collections.unmodifiableMap(freeTaskNodes);
    }

    @Override
    public List<TaskNode> getFreeTasks() { // Pass a copy of the list instead to stay immutable? would be slower
        return null;
    }

    @Override
    public TaskNode getTask(String taskName) { // TODO... handle instance where task not in graph
        return this.taskNodes.get(taskName); // Okay to pass object straight as ImmutableTaskNode is also Immutable
    }

    @Override
    public TaskGraph getOriginalTaskGraph() {
        return this;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-----------------------------\n");
        stringBuilder.append("Immutable Task Graph:\n");

        stringBuilder.append("Free Task Nodes: \n");
        for (TaskNode freeTaskNode: this.freeTaskNodes.values()) {
            stringBuilder.append(freeTaskNode).append("\n"); // Implicit toString() method call
        }

        stringBuilder.append("General Task Nodes: \n");
        for (TaskNode taskNode: this.taskNodes.values()) {
            if (!this.freeTaskNodes.containsKey(taskNode.getTaskID())) {
                stringBuilder.append(taskNode).append("\n"); // Implicit toString() method call
            }
        }

        stringBuilder.append("-----------------------------\n");
        return stringBuilder.toString();
    }
}
