package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class OriginalTaskNodeState extends TaskNodeState {

    private final int taskID;
    private final int taskCost;
    private final int[][] childLinks;

    /**
     * TODO...
     */
    public OriginalTaskNodeState(int taskID, int taskCost, int numLinks, int[][] childLinks, int numProcessors) {
        super(numLinks, new int[numProcessors]);
        this.taskID = taskID;
        this.taskCost = taskCost;
        this.childLinks = childLinks;
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
    public int[][] getChildLinks() {
        return this.childLinks;
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
