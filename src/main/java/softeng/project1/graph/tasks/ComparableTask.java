package softeng.project1.graph.tasks;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * @author Adam Sinclair
 * @version 2.0
 * @since 1.0
 * A utility class to make it easier to compare GraphStream nodes,
 * to determine equivalent nodes. This is done by hashing the parent and children edge sets,
 * and also by comparing their weights.
 */
public class ComparableTask implements Comparable<ComparableTask> {
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final Node task;
    private final int taskWeight;
    private final HashMap<Node, Integer> parentEdgeHashMap;
    private final HashMap<Node, Integer> childEdgeHashMap;

    public ComparableTask(Node task, int taskWeight) {
        this.task = task;
        this.taskWeight = taskWeight;

        // retrieves the edges coming from parents of this task
        ArrayList<Edge> parents = task.enteringEdges().collect(Collectors.toCollection(ArrayList::new));
        this.parentEdgeHashMap = new HashMap<>();

        for (Edge parentEdge : parents) {
            Node parentNode = parentEdge.getSourceNode();
            int weight = ((Double) parentEdge.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY)).intValue();
            // Creates a mapping of parent nodes to weights, as both must be the same for equivalent children
            parentEdgeHashMap.put(parentNode, weight);
        }

        // retrieves the edges going to children of this task
        ArrayList<Edge> children = task.leavingEdges().collect(Collectors.toCollection(ArrayList::new));
        this.childEdgeHashMap = new HashMap<>();

        for (Edge childEdge : children) {
            Node childNode = childEdge.getTargetNode();
            int weight = ((Double) childEdge.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY)).intValue();
            // Creates a mapping of children nodes to weights, as both must be the same for equivalent children
            childEdgeHashMap.put(childNode, weight);
        }
    }

    /**
     * @return the task belonging to this comparable object
     */
    public Node getTask() {
        return task;
    }

    /**
     * Used to compare two ComparableTask objects, so that they can be sorted and checked for equivalence.
     * For equivalent children, they must have equal:
     * - task weight
     * - parent tasks
     * - children tasks
     * - parent communication weights
     * - children communication weights
     *
     * @param o the other ComparableTask to compare to
     * @return less than o if this is less than o
     * 0 if this is equal to o
     * greater than 0 if this is greater than o.
     */
    @Override
    public int compareTo(@NotNull ComparableTask o) {
        if (this.taskWeight != o.taskWeight) {
            return this.taskWeight - o.taskWeight;
        } else if (!this.parentEdgeHashMap.equals(o.parentEdgeHashMap)) {
            return this.parentEdgeHashMap.hashCode() - o.parentEdgeHashMap.hashCode();
        } else if (!this.childEdgeHashMap.equals(o.childEdgeHashMap)) {
            return this.childEdgeHashMap.hashCode() - o.childEdgeHashMap.hashCode();
        } else {
            // Equivalent children.
            return 0;
        }
    }
}
