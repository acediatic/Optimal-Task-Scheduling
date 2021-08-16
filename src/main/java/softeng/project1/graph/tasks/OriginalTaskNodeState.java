package softeng.project1.graph.tasks;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * <p>
 * Implementation of TaskNode that specifically represents the state of a task before any tasks have been scheduled.
 * OriginalTaskNodeState offers a jumping off point for the creation of later TaskNode objects, as, unlike
 * ChangedTaskNodeState, OriginalTaskNodeState can be directly initialised.
 * <p>
 * OriginalTaskNodeState also stores all relevant data regarding the task. Only one OriginalTaskNodeState object should
 * be initialised per Task so storing this information doesn't lead to any memory issues.
 * Other implementations of TaskNode are expected to store a reference to their task's OriginalTaskNodeState and
 * redirect calls for static data to it, rather than take up memory space storing them themselves.
 * <p>
 * OriginalTaskNodeState offers all the functionality described by TaskNode however many of the methods simply
 * return static values rather than do any calculations. This occurs as many of the values can be assumed due to the
 * fact that OriginalTaskNodeState represents a task before any placements have occurred.
 * <p>
 * Note that unlike TaskNodes in general, taskIDs should be unique within the set of all OriginalTaskNodeStates.
 * Instantiating duplicate OriginalTaskNodeStates is memory intensive due to all task information being directly stored
 * in them.
 * <p>
 * As with all TaskNodes OriginalTaskNodeState should be considered immutable.
 */
public class OriginalTaskNodeState extends TaskNodeState {

    // Task data fields, should all be immutable
    private final int taskID; // ID of task
    private final int taskCost; // Cost of Task
    private final int[][] childLinks; // Array of arrays representing outgoing edges, don't expose this directly
    private final int maxCommunicationCost; // Most expensive communication cost in the childLinks set
    // If there are no child links then this = 0
    private int bottomLevel; // TODO... remember what bottom level is

    /**
     * Standard constructor for OriginalTaskNodeState objects.
     * This should be the ONLY public constructor available for all TaskNode implementations! All other
     * implementations should be instantiated via the copyAndSetPrerequisite method.
     * <p>
     * For speed reasons the processor prerequisites and child links are stored privately as arrays rather than
     * immutable lists. As such care should be taken to never expose these fields directly.
     *
     * @param taskID        : The (unique within the set of OriginalTaskNodeStates) integer ID number that represents which
     *                      task the OriginalTaskNodeState represents the original state of.
     * @param taskCost      : The processing cost/weight/length of the task that this object represents a state of.
     * @param numLinks      : The number of incoming UNSCHEDULED links/edges/prerequisites which connect to the task that
     *                      this object represents a state of.
     * @param childLinks    : An array of integer arrays that store information regarding outgoing
     *                      links/edges/prerequisites that stem from the task that this object represents a state of.
     *                      The top level array is expected to be ordered arbitrarily, however the data arrays should
     *                      follow the format:
     *                      childLink[0] -> child TaskNode's task ID.
     *                      childLink[1] -> communication cost of transferring data from this task once finished to a
     *                      different processor and then preparing it for use by the child task.
     * @param numProcessors : The number of processors available for the task to possibly be scheduled on.
     */
    public OriginalTaskNodeState(int taskID, int taskCost, int numLinks, int[][] childLinks, int numProcessors) {
        super(numLinks, new int[numProcessors]);
        this.taskID = taskID;
        this.taskCost = taskCost;
        this.childLinks = childLinks;

        // Calculating max cost, could possibly be moved out of constructor but not really a big deal
        // For speed reasons we store this value rather than calculate it every time it's needed
        int tempCommunicationCost = 0;
        for (int[] childLink : childLinks) {
            if (tempCommunicationCost < childLink[1]) {
                tempCommunicationCost = childLink[1];
            }
        }
        this.maxCommunicationCost = tempCommunicationCost;
    }

    public void setBottomLevel(int bottomLevel) {
        // Makes bottom level essentially final, but can be called after constructor.
        if (bottomLevel == 0) {
            this.bottomLevel = bottomLevel;
        }
    }

    /**
     * Generates a new state of the task representing the state that stems from this state after one of the task's
     * parent tasks is scheduled into a processor.
     * When a parent is scheduled its children tasks are changed in two ways:
     * - The number of incoming links from UNSCHEDULED parents is reduced by one
     * - The position in each processor where all data being sent from SCHEDULED parent tasks is first available may
     * be increased depending on: Where the parent was scheduled/what processor it was scheduled on, the cost of
     * completing the parent task, and the communication cost of the link.
     * As OriginalTaskNodeStates will always have zero values for it's processor prerequisites (as no tasks have yet
     * been scheduled), no calculations are needed for finding the data ready positions, we can directly pass the
     * locations provided by the scheduled parent.
     *
     * @param parentPrerequisites : The first time/location on every processor where the data from the scheduled parent
     *                            task becomes available.
     * @return : A new TaskNodeState representing the state of the task after another of its parent tasks has been
     * scheduled.
     */
    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        return new ChangedTaskNodeState(
                this,
                this.numUnscheduledLinks - 1,
                parentPrerequisites // Don't need to calculate max because original prerequisites all 0
        );
    }

    // ------ Standard Getter Methods -------

    /**
     * Returns the ID unique to the wrapped Task.
     * Note that IDs are NOT unique between TaskNodes as they describe the states of Tasks not just the Tasks themselves
     * Task IDs should be in the range of 0-255 in order to ensure that issues with hashing do not occur.
     *
     * @return : ID of the Task whose state the Task Node represents.
     */
    @Override
    public int getTaskID() {
        return this.taskID;
    }

    /**
     * Returns the amount of time required for the task to complete when actively being run on a processor.
     * Note that task costs/length should exist in the range 0 - 255 to ensure that hashing issues do not occur.
     *
     * @return : The processing cost / task length of the Task whose state the Task Node represents.
     */
    @Override
    public int getTaskCost() {
        return this.taskCost;
    }

    /**
     * @return : All outgoing child links/edges/prerequisite that stem from the TaskNode. For speed reasons these are
     * stored as arrays with the following format:
     * childLinks[0] => The task ID of the child task
     * childLinks[1] => The cost/time required for communicating/transferring the required data from this task
     * to a DIFFERENT processor than the one this task was scheduled on.
     */
    @Override
    public int[][] getChildLinks() {
        return this.childLinks.clone();
    }

    /**
     * @return : // TODO... Remember what bottom level is
     */
    @Override
    public int getBottomLevel() {
        return this.bottomLevel;
    }

    /**
     * @return : The largest/longest communication cost out of all the child links that stem from the task. If the task
     * has no child links this method returns 0.
     */
    @Override
    public int getMaxCommunicationCost() {
        return this.maxCommunicationCost;
    }
}
