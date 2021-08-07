package softeng.project1.graph.tasks;

/**
 * TODO...
 */
public class OriginalTaskNodeState extends TaskNodeState {

    private final int taskID;
    private final int taskCost;
    private final int[][] childLinks;
    private final int maxCommunicationCost;
    private final int bottomLevel;

    /**
     * TODO...
     */
    public OriginalTaskNodeState(int taskID, int taskCost, int numLinks, int[][] childLinks, int bottomLevel, int numProcessors) {
        super(numLinks, new int[numProcessors]);
        this.taskID = taskID;
        this.taskCost = taskCost;
        this.childLinks = childLinks;

        // Calculating max cost, could possibly be moved out of constructor but not really a big deal
        int tempCommunicationCost = 0;
        for (int[] childLink : childLinks) {
            if (tempCommunicationCost < childLink[1]) {
                tempCommunicationCost = childLink[1];
            }
        }
        this.maxCommunicationCost = tempCommunicationCost;

        this.bottomLevel = bottomLevel;
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
    public int getBottomLevel() {
        return this.bottomLevel;
    }

    @Override
    public int getMaxCommunicationCost() {
        return this.maxCommunicationCost;
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
