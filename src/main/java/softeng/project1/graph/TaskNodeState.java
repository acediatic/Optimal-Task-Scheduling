package softeng.project1.graph;

/**
 * TODO...
 */
public class TaskNodeState implements TaskNode {

    private final TaskNode originalTaskNode;
    private int[] processorPrerequisites;

    public TaskNodeState(TaskNode previousState) {
        this.originalTaskNode = previousState.getOriginalTaskNode();
        this.processorPrerequisites = new int[10]; // TODO... base size off of num processors
    }

    @Override
    public int getTaskID() {
        return originalTaskNode.getTaskID();
    }

    @Override
    public int getTaskCost() {
        return originalTaskNode.getTaskCost();
    }

    @Override
    public TaskNode getOriginalTaskNode() {
        return this.originalTaskNode;
    }

    @Override
    public int getProcessorPrerequisite(int processorID) {
        return this.processorPrerequisites[processorID];
    }
}
