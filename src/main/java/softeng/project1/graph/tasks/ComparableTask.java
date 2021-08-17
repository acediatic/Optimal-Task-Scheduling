package softeng.project1.graph.tasks;

import org.graphstream.graph.Edge;
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
    private final Node task;
    private final int taskWeight;
    private final int parentSetHash;
    private final int childSetHash;

    // TODO confirm the edge object is the same, and so can be used for equality comparison.
    // And that it includes edge weights.
    public ComparableTask(Node task, int taskWeight, Edge[] childLinks, Edge[] parentLinks) {
        this.task = task;
        this.taskWeight = taskWeight;

        Collection<? extends Node> parentEdges = (Collection<? extends Node>) task.enteringEdges().collect(Collectors.toCollection(ArrayList::new));
        HashSet<Node> parentSet = new HashSet<>(parentEdges.size());
        parentSet.addAll(parentEdges);
        this.parentSetHash = parentSet.hashCode();

        Collection<? extends Node> childEdges = (Collection<? extends Node>) task.leavingEdges().collect(Collectors.toCollection(ArrayList::new));
        HashSet<Node> childSet = new HashSet<>(childEdges.size());
        childSet.addAll(childEdges);
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
        } else if (false) { // TODO check for weights
            return 0;
        } else {
            return 0;
        }
    }
}
