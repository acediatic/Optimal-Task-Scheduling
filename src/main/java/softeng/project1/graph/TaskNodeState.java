package softeng.project1.graph;

/**
 * TODO...
 */
public class TaskNodeState implements TaskNode {

    private final TaskNode originalTaskNode;

    public TaskNodeState(TaskNode previousState) {
        this.originalTaskNode = previousState.getOriginalTaskNode();
    }

    @Override
    public String getTaskName() {
        return originalTaskNode.getTaskName();
    }

    @Override
    public int getTaskCost() {
        return originalTaskNode.getTaskCost();
    }

    @Override
    public TaskNode getOriginalTaskNode() {
        return this.originalTaskNode;
    }
}
