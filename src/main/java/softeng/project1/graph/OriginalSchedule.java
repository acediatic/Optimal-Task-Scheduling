package softeng.project1.graph;

import softeng.project1.graph.processors.OriginalProcessorsState;
import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.OriginalTaskNode;
import softeng.project1.graph.tasks.TaskNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class OriginalSchedule implements Schedule {

    private final Map<Integer, OriginalTaskNode> taskNodes;
    private final Map<Integer, OriginalTaskNode> freeTaskNodes;
    private final Processors processors;

    /**
     * TODO...
     */
    public OriginalSchedule(Map<Integer, OriginalTaskNode> taskNodes, Map<Integer, OriginalTaskNode> freeTaskNodes, int numProcessors) {
        // Making these immutable, note that the underlying map can still be changed.
        this.taskNodes = Collections.unmodifiableMap(taskNodes);
        this.freeTaskNodes = Collections.unmodifiableMap(freeTaskNodes);
        this.processors = new OriginalProcessorsState(numProcessors);
    }

    @Override
    public long getHashKey() {
        return 0; // TODO...
    }

    @Override
    public int getIdleTime() {
        return 0; // TODO...
    }

    @Override
    public int getMaxBottomLevel() {
        return 0; // TODO...
    }

    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        // We directly check the object as the only object with the same values as the original is itself.
        return this == otherSchedule;
    }

    @Override
    public int getMaxDataReadyTime() {
        return 0; // TODO...
    }

    @Override
    public List<Schedule> expand() {
        return null; // TODO...
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-----------------------------\n");
        stringBuilder.append("Original Schedule:\n");

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
