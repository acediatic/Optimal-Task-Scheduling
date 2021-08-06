package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class OriginalTaskNodeState extends TaskNodeState {

    private final int taskID;
    private final int taskCost;

    /**
     * TODO...
     */
    public OriginalTaskNodeState(int taskID, int taskCost, int numLinks, int numProcessors) {
        super(numLinks, new int[numProcessors]);
        this.taskID = taskID;
        this.taskCost = taskCost;
    }

    @Override
    public int getTaskID() {
        return this.taskID;
    }

    @Override
    public int getTaskCost() {
        return this.taskCost;
    }

    @Override
    public TaskNode getOriginalTaskNode() {
        return this;
    }

    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        return new ChangedTaskNodeState(
                this,
                this.numLinks - 1,
                parentPrerequisites // Don't need to calculate max because original prerequisites all 0
        );
    }
}
