package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class OriginalTaskNode implements TaskNode {

    private final int taskID;
    private final int taskCost;
    private final int numLinks;
    private final int[] initialProcessorPrerequisites;

    /**
     * TODO...
     */
    public OriginalTaskNode(int taskID, int taskCost, int numLinks, int numProcessors) {
        this.taskID = taskID;
        this.taskCost = taskCost;
        this.numLinks = numLinks;
        this.initialProcessorPrerequisites = new int[numProcessors];
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
    public boolean isFree() {
        return numLinks == 0;
    }

    @Override
    public int getProcessorPrerequisite(int processorID) {
        // Always 0 because original task nodes have never had any prerequisites filled
        return 0;
    }

    @Override
    public int[] getAllPrerequisites() {
        return this.initialProcessorPrerequisites;
    }

    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        return new TaskNodeState(
                this,
                this.numLinks - 1,
                parentPrerequisites // Don't need to calculate max because original prerequisites all 0
        );
    }

    @Override
    public String toString() {
        @SuppressWarnings("StringBufferReplaceableByString") StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("######################\n");

        stringBuilder
            .append("Immutable Task Node:\n")
            .append("Task ID: ").append(this.taskID).append("\n")
            .append("Task Cost: ").append(this.taskCost).append("\n");
        stringBuilder.append("######################\n");

        return stringBuilder.toString();
    }

}
