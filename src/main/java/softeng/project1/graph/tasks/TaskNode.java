package softeng.project1.graph.tasks;

/**
 * Interface describing a specific TaskNode in the graph representation of the scheduling problem.
 * Instances of TaskNode should be immutable and final so that they can be accessed by multiple threads at once.
 */
public interface TaskNode {

    int getTaskID();

    int getTaskCost();

    TaskNode getOriginalTaskNode();

    int getProcessorPrerequisite(int processorID);

    @Override
    String toString();

}
