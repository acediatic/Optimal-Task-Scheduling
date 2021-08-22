package softeng.project1.graph.tasks;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @see OriginalTaskNodeState
 * <p>
 * In order to reduce memory space needed ChangedTaskNodeStates store a reference to their task's original state and
 * source all static task data from it. This includes:
 * - Task ID
 * - Task Cost/Weight
 * - Task's outgoing child links
 * - Task's bottom level
 * - Task's max communication cost
 * <p>
 * As with all TaskNodeStates, ChangedTaskNodeStates should be considered immutable.
 * @since 1.8
 * <p>
 * Implementation of TaskNodeState that represents the state of a task after at least one of the task's parents has been
 * scheduled into a processor. For the task state before any parents are scheduled:
 */
public class ChangedTaskNodeState extends TaskNodeState {

    // Immutable data field representing the state of the of this object's task before any tasks have been scheduled
    // Kept as a reference so that we can source static task data from it rather than store it ourselves
    private final TaskNode originalTaskNode;

    /**
     * Protected constructor for use by other TaskNodeStates ONLY!
     * To protect the ChangedTaskNodeStates immutability all ChangedTaskNodeStates should be instantiated through the
     * copyAndSetPrerequisite method described by TaskNode.
     * <p>
     * TaskNodeState implementations expect for their to be at least one processor. Passing in an empty array for
     * processorPrerequisites may cause unexpected outcomes or crashes.
     * For speed reasons, error checking for invalid data is not handled here.
     *
     * @param originalTaskNode       : The state of the the task before any parent tasks have been scheduled
     * @param numLinks               : Number of task's parent tasks who aren't currently scheduled.
     * @param processorPrerequisites : Earliest moment in each processor when all data from currently scheduled parents
     *                               is available.
     */
    protected ChangedTaskNodeState(TaskNode originalTaskNode, int numLinks, int[] processorPrerequisites) {
        super(numLinks, processorPrerequisites);
        this.originalTaskNode = originalTaskNode;
    }

    /**
     * Generates a new state of the task representing the state that stems from this state after one of the task's
     * parent tasks is scheduled into a processor.
     * When a parent is scheduled its children tasks are changed in two ways:
     * - The number of incoming links from UNSCHEDULED parents is reduced by one
     * - The position in each processor where all data being sent from SCHEDULED parent tasks is first available may
     * be increased depending on: Where the parent was scheduled/what processor it was scheduled on, the cost of
     * completing the parent task, and the communication cost of the link.
     *
     * @param parentPrerequisites : The first time/location on every processor where the data from the scheduled parent
     *                            task becomes available.
     * @return : A new TaskNodeState representing the state of the task after another of its parent tasks has been
     * scheduled.
     */
    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        int[] newPrerequisites = new int[this.processorPrerequisites.length];

        // If any prerequisite locations are later than the current earliest moment, then the earliest moment is changed
        // to that location instead.
        for (int i = 0; i < this.processorPrerequisites.length; i++) {
            newPrerequisites[i] = Math.max( // Taking the larger of the two, possible to do this faster?
                    this.processorPrerequisites[i],
                    parentPrerequisites[i]
            );
        }

        // Creating a new ChangedTaskNodeState to represent the new task state
        return new ChangedTaskNodeState(
                this.originalTaskNode,
                this.numUnscheduledLinks - 1, // -1 as one of its parents have been scheduled
                newPrerequisites
        );
    }

    /**
     * Returns the ID unique to the wrapped Task.
     * Note that IDs are NOT unique between TaskNodes as they describe the states of Tasks not just the Tasks themselves
     * Task IDs should be in the range of 0-255 in order to ensure that issues with hashing do not occur.
     *
     * @return : ID of the Task whose state the Task Node represents.
     */
    @Override
    public short getTaskID() {
        // retrieving info from where it's stored in the original task node state
        return this.originalTaskNode.getTaskID();
    }

    /**
     * Returns the amount of time required for the task to complete when actively being run on a processor.
     * Note that task costs/length should exist in the range 0 - 255 to ensure that hashing issues do not occur.
     *
     * @return : The processing cost / task length of the Task whose state the Task Node represents.
     */
    @Override
    public int getTaskCost() {
        // retrieving info from where it's stored in the original task node state
        return this.originalTaskNode.getTaskCost();
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
        // retrieving info from where it's stored in the original task node state
        return this.originalTaskNode.getChildLinks();
    }

    /**
     * The sum of task weights for this node, and all other nodes on the critical path from this node to the exit node.
     *
     * @return : the bottom level for this node.
     */
    @Override
    public int getBottomLevel() {
        // retrieving info from where it's stored in the original task node state
        return this.originalTaskNode.getBottomLevel();
    }

    /**
     * @return : The largest/longest communication cost out of all the child links that stem from the task. If the task
     * has no child links this method returns 0.
     */
    @Override
    public int getMaxCommunicationCost() {
        // retrieving info from where it's stored in the original task node state
        return this.originalTaskNode.getMaxCommunicationCost();
    }
}
