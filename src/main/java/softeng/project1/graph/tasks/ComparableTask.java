package softeng.project1.graph.tasks;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;


/**
 * A utility class to make it easier to compare GraphStream nodes,
 * to determine equivalent nodes.
 */
public class ComparableTask implements Comparable<ComparableTask> {
    private final Node task;
    private final int taskWeight;
    private final Edge[] childLinks;
    private final Edge[] parentLinks;

    // TODO confirm the edge object is the same, and so can be used for equality comparison.
    // And that it includes edge weights.
    public ComparableTask(Node task, int taskWeight, Edge[] childLinks, Edge[] parentLinks) {
        this.task = task;
        this.taskWeight = taskWeight;
        this.childLinks = childLinks;
        this.parentLinks = parentLinks;
    }

    public Node getTask() {
        return task;
    }


    // TODO confirm this is all kosher.
    @Override
    public int compareTo(@NotNull ComparableTask o) {
        if (this.taskWeight != o.taskWeight) {
            return this.taskWeight - o.taskWeight;
        } else if (this.childLinks.length != o.childLinks.length) {
            return this.childLinks.length - o.childLinks.length;
        } else if (this.parentLinks.length != o.parentLinks.length) {
            return this.parentLinks.length - o.parentLinks.length;
        } else {
            for (int i = 0; i < this.childLinks.length; i++) {
                if (this.childLinks[i] != o.childLinks[i]) {
                    return -1;
                }
            }

            for (int i = 0; i < this.parentLinks.length; i++) {
                if (this.parentLinks[i] != o.parentLinks[i]) {
                    return -1;
                }
            }
            return 0;
        }
    }
}
