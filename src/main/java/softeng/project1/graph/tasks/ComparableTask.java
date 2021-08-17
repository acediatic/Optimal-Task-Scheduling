package softeng.project1.graph.tasks;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * A utility class to make it easier to compare GraphStream nodes,
 * to determine equivalent nodes.
 */
public class ComparableTask implements Comparable<ComparableTask> {
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final Node task;
    private final int taskWeight;
    private final HashMap<Node, Integer> parentEdgeHashMap;
    private final HashMap<Node, Integer> childEdgeHashMap;

    // TODO confirm the edge object is the same, and so can be used for equality comparison.
    public ComparableTask(Node task, int taskWeight) {
        this.task = task;
        this.taskWeight = taskWeight;

        ArrayList<Edge> parents = task.enteringEdges().collect(Collectors.toCollection(ArrayList::new));
        this.parentEdgeHashMap = new HashMap<>();

        for (Edge parentEdge : parents) {
            Node parentNode = parentEdge.getSourceNode();
            int weight = 0;//(int) parentEdge.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);
            parentEdgeHashMap.put(parentNode, weight);
        }


        ArrayList<Edge> children = task.leavingEdges().collect(Collectors.toCollection(ArrayList::new));
        this.childEdgeHashMap = new HashMap<>();

        for (Edge childEdge : children) {
            Node childNode = childEdge.getTargetNode();
            int weight = 0;//(int) childEdge.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);
            childEdgeHashMap.put(childNode, weight);
        }
    }

    public Node getTask() {
        return task;
    }

    // TODO confirm this is all kosher.
    @Override
    public int compareTo(@NotNull ComparableTask o) {
        if (this.taskWeight != o.taskWeight) {
            return this.taskWeight - o.taskWeight;
        } else if (!this.parentEdgeHashMap.equals(o.parentEdgeHashMap)) {
            return this.parentEdgeHashMap.hashCode() - o.parentEdgeHashMap.hashCode();
        } else if (this.childEdgeHashMap.equals(o.parentEdgeHashMap)) {
            return this.childEdgeHashMap.hashCode() - o.parentEdgeHashMap.hashCode();
        } else {
            // Equivalent children.
            return 0;
        }
    }
}
