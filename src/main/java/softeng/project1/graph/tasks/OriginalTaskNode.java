package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class OriginalTaskNode implements TaskNode {

    private final int taskID;
    private final int taskCost;

    /**
     * TODO...
     */
    public OriginalTaskNode(int taskID, int taskCost) {
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
        return null;
    }

    @Override
    public int getProcessorPrerequisite(int processorID) {
        return 0; //TODO..
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
