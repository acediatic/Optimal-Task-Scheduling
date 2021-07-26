package softeng.project1.graph;

/**
 * TODO...
 */
public class ImmutableTaskNode implements TaskNode {

    private final String taskName;
    private final int taskCost;

    /**
     * TODO...
     */
    public ImmutableTaskNode(String taskName, int taskCost) {
        this.taskName = taskName;
        this.taskCost = taskCost;
    }

    @Override
    public String getTaskName() {
        return this.taskName;
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
    public String toString() {
        @SuppressWarnings("StringBufferReplaceableByString") StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("######################\n");

        stringBuilder
            .append("Immutable Task Node:\n")
            .append("Task Name: ").append(this.taskName).append("\n")
            .append("Task Cost: ").append(this.taskCost).append("\n");
        stringBuilder.append("######################\n");

        return stringBuilder.toString();
    }

}
