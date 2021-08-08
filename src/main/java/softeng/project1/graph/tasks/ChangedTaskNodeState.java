package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class ChangedTaskNodeState extends TaskNodeState {

    private final TaskNode originalTaskNode;

    protected ChangedTaskNodeState(TaskNode originalTaskNode, int numLinks, int[] processorPrerequisites) {
        super(numLinks, processorPrerequisites);
        this.originalTaskNode = originalTaskNode;
    }

    @Override
    public int getTaskID() {
        return this.originalTaskNode.getTaskID();
    }

    @Override
    public int getTaskCost() {
        return this.originalTaskNode.getTaskCost();
    }

    @Override
    public int[][] getChildLinks() {
        return this.originalTaskNode.getChildLinks();
    }

    @Override
    public int getBottomLevel() {
        return this.originalTaskNode.getBottomLevel();
    }

    @Override
    public int getMaxCommunicationCost() {
        return this.originalTaskNode.getMaxCommunicationCost();
    }

    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        int[] newPrerequisites = new int[this.processorPrerequisites.length];

        for (int i = 0; i < this.processorPrerequisites.length; i++) {
            newPrerequisites[i] = Math.max(
                    this.processorPrerequisites[i],
                    parentPrerequisites[i]
            );
        }

        return new ChangedTaskNodeState(
                this.originalTaskNode,
                this.numUnscheduledLinks - 1,
                newPrerequisites
        );
    }
}
