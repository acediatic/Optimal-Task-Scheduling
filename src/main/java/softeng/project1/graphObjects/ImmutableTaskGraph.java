package softeng.project1.graphObjects;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImmutableTaskGraph implements TaskGraph {

    private final Map<String, ImmutableTaskNode> taskNodes;
    private final Map<String, ImmutableTaskNode> freeTaskNodes;

    public ImmutableTaskGraph(Map<String, ImmutableTaskNode> taskNodes, Map<String, ImmutableTaskNode> freeTaskNodes) {
        this.taskNodes = Collections.unmodifiableMap(taskNodes); // Making these immutable, note that the underlying map can still be changed.
        this.freeTaskNodes = Collections.unmodifiableMap(freeTaskNodes); 
    }

    @Override
    public List<TaskNode> getFreeTasks() { // Pass a copy of the list instead to stay immutable? would be slower 
        return null;
    }

    @Override
    public TaskNode getTask(String taskName) { // TODO... handle instance where task not in graph, currently returns null
        return this.taskNodes.get(taskName); // Okay to pass object straight as ImmutableTaskNode is also Immutable
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-----------------------------\n");
        stringBuilder.append("Immutable Task Graph:\n");

        stringBuilder.append("Free Task Nodes: \n");
        for (TaskNode freeTaskNode: this.freeTaskNodes.values()) {
            stringBuilder
            .append(freeTaskNode) // Implicit toString() method call
            .append("\n");
        }

        stringBuilder.append("General Task Nodes: \n");
        for (TaskNode taskNode: this.taskNodes.values()) {
            if (!this.freeTaskNodes.containsKey(taskNode.getTaskName())) {
                stringBuilder
                .append(taskNode) // Implicit toString() method call 
                .append("\n");
            }
        }

        stringBuilder.append("-----------------------------\n");
        return stringBuilder.toString();
    }
    
}
