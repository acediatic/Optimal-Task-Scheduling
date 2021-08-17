package softeng.project1.graph.tasks;

import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;


/**
 * A utility class to make it easier to compare GraphStream nodes,
 * to determine equivalent nodes.
 */
public class ComparableTask implements Comparable<ComparableTask> {
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final Node task;
    private final int taskWeight;
    private final int parentSetHash;
    private final int childSetHash;
    private final Collection<? extends Node> parents;
    private final Collection<? extends Node> children;

    // TODO confirm the edge object is the same, and so can be used for equality comparison.
    public ComparableTask(Node task, int taskWeight) {
        this.task = task;
        this.taskWeight = taskWeight;

        this.parents = (Collection<? extends Node>) task.enteringEdges().collect(Collectors.toCollection(ArrayList::new));
        HashSet<Node> parentSet = new HashSet<>(parents.size());
        parentSet.addAll(parents);
        this.parentSetHash = parentSet.hashCode();

        this.children = (Collection<? extends Node>) task.leavingEdges().collect(Collectors.toCollection(ArrayList::new));
        HashSet<Node> childSet = new HashSet<>(children.size());
        childSet.addAll(children);
        this.childSetHash = childSet.hashCode();
    }

    public Node getTask() {
        return task;
    }

    // TODO confirm this is all kosher.
    @Override
    public int compareTo(@NotNull ComparableTask o) {
        if (this.taskWeight != o.taskWeight) {
            return this.taskWeight - o.taskWeight;
        } else if (this.childSetHash != o.childSetHash) {
            return this.childSetHash - o.childSetHash;
        } else if (this.parentSetHash != o.parentSetHash) {
            return this.parentSetHash - o.parentSetHash;
        } else {
            // check communication weights
            ArrayList<Collection<? extends Node>> parentsAndChildren = new ArrayList<>();
            parentsAndChildren.add(this.children);
            parentsAndChildren.add(this.parents);

            for (Collection<? extends Node> edgeCollection : parentsAndChildren) {
                for (Node edgeNode : edgeCollection) {
                    double thisWeight = (double) this.task.getEdgeBetween(edgeNode).getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);
                    double oWeight = (double) o.task.getEdgeBetween(edgeNode).getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);

                    if (thisWeight - oWeight != 0) {
                        return (int) (thisWeight - oWeight);
                    }
                }
            }
            // Equivalent children.
            return 0;
        }
    }
}
