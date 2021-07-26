package softeng.project1.graph;

/**
 * Interface describing a specific TaskNode in the graph representation of the scheduling problem.
 * Instances of TaskNode should be immutable and final so that they can be accessed by multiple threads at once.
 */
public interface TaskNode {

    String getTaskName();

    int getTaskCost();

    TaskNode getOriginalTaskNode();

    @Override
    String toString();

}
