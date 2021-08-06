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
        return originalTaskNode.getTaskID();
    }

    @Override
    public int getTaskCost() {
        return originalTaskNode.getTaskCost();
    }

    @Override
    public int[][] getChildLinks() {
        return this.originalTaskNode.getChildLinks();
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
                this.numLinks - 1,
                newPrerequisites
        );
    }
}
