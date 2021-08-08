package softeng.project1.graph.tasks;

public abstract class TaskNodeState implements TaskNode {

    protected final int numLinks;
    protected final int[] processorPrerequisites;

    protected TaskNodeState(int numLinks, int[] processorPrerequisites) {
        this.numLinks = numLinks;
        this.processorPrerequisites = processorPrerequisites;
    }

    @Override
    public boolean isFree() {
        return this.numLinks == 0;
    }

    @Override
    public int getProcessorPrerequisite(int processorID) {
        return this.processorPrerequisites[processorID];
    }

    @Override
    public int[] getAllPrerequisites() {
        return this.processorPrerequisites;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("TaskNodeState:\n")
                .append("numLinks: ").append(this.numLinks).append("\n")
                .append("Processor Prerequisites:\n");

        for (int i = 0; i < processorPrerequisites.length; i++) {
            builder.append(i).append(" - ").append(processorPrerequisites[i]);
        }
        return builder.toString();
    }

}
