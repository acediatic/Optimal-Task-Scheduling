package softeng.project1.graph.tasks;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * The TaskNode interface describes a specific task in a partial schedule.
 * In the interests of saving space, task nodes are expected to be backed by a unique original task node which stores
 * all static data describing the task. Specifically:
 * - Task ID
 * - Task Cost/Length
 *
 * This data must be available through all implementations of Task Node as access to the original graph is not always
 * available.
 *
 * As Task Nodes describe the state of a task (A.K.A what partial schedule it resides in), functionality is provided
 * to retrieve data describing the current influence of possibly scheduled parent tasks.
 * Task Nodes provide the ability to determine whether or not it is in a 'free' state (If all of its parents have been
 * scheduled) and can describe the earliest possible position in each processor where all data provided by scheduled
 * parents is available.
 * Data regarding specific parents is not provided by this interface.
 *
 * Generating new TaskNode objects should be handled through the copyAndSetPrerequisite method in order to ensure that
 * all task nodes are immutable and duplicates aren't created. Only specific original nodes should be directly
 * instantiated.
 */
public interface TaskNode {

    /**
     * Returns the ID unique to the wrapped Task.
     * Note that IDs are NOT unique between TaskNodes as they describe the states of Tasks not just the Tasks themselves
     * Task IDs should be in the range of 0-255 in order to ensure that issues with hashing do not occur.
     *
     * @return : ID of the Task whose state the Task Node represents.
     */
    int getTaskID();

    /**
     * Returns the amount of time required for the task to complete when actively being run on a processor.
     * Note that task costs/length should exist in the range 0 - 255 to ensure that hashing issues do not occur.
     *
     * @return : The processing cost / task length of the Task whose state the Task Node represents.
     */
    int getTaskCost();

    /**
     * TODO...
     */
    int[][] getChildLinks();

    /**
     * Returns whether or not the task is in a 'free' state. A task if free if all of it's parents have been scheduled.
     *
     * @return : Whether or not all of the tasks parents have been scheduled.
     */
    boolean isFree();

    /**
     * Returns the earliest possible location on the processor with the given ID, where all data provided by its
     * SCHEDULED parents is available.
     * Note that querying this method when the Task Node is not free may return useless values.
     *
     * @param processorID : The ID of the processor on which the prerequisite location is being returned.
     * @return : The time/location on the specific processor when all data required by the task is first available.
     */
    int getProcessorPrerequisite(int processorID);

    /**
     * Returns the earliest possible location on each array where all data provided by the SCHEDULED parent tasks is
     * available.
     * Note that querying this method while the Task Node is not free may return useless values.
     *
     * @return : An array containing the time/location on each processor when all data provided by the task's parent
     * tasks first becomes available.
     */
    int[] getAllPrerequisites();

    /**
     * Creates a new Task Node object representing the next state of the Task after one of it's parent tasks has been
     * scheduled in a processor.
     *
     * @param parentPrerequisites : The first time/location on every processor where the data from the scheduled parent
     *                              task becomes available.
     * @return : A copy of the current Task Node with the addition of the data regarding a specific parent's scheduling.
     */
    TaskNode copyAndSetPrerequisite(int[] parentPrerequisites);

    @Override
    String toString();

}
