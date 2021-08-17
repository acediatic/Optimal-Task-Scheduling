package softeng.project1.graph.tasks;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Abstract implementation of TaskNode representing a task at a specific point in time/specific state.
 * As TaskNodeState represents a state, it should be a completely immutable object.
 * TaskNodeState stores the following data specific to a task's state:
 * - numUnscheduledLinks =>  Number of incoming links from parents who have not yet been scheduled.
 * - processorPrerequisites => Earliest point in each processor where data from all SCHEDULED parent tasks is available.
 *
 * For speed reasons the processorPrerequisites data is stored in an array. In order for TaskNodeStates to remain
 * immutable care must be taken not to directly expose the array.
 */
public abstract class TaskNodeState implements TaskNode {

    // Immutable data fields
    protected final int numUnscheduledLinks; // Number of task's parent tasks who aren't currently scheduled
    protected final int[] processorPrerequisites;   // Earliest moment in each processor when all data from currently
                                                    // scheduled parents is available


    /**
     * Protected constructor for use by other TaskNodeState objects only.
     * To ensure immutability non-original TaskNodeStates should only be generated via the copyAndSetPrerequisite method
     * described in TaskNode.
     *
     * TaskNodeState expects for their to be at least one processor. Passing in an empty array for
     * processorPrerequisites may cause unexpected outcomes or crashes.
     * For speed reasons, error checking for invalid data is not handled here.
     *
     * @param numUnscheduledLinks : Number of task's parent tasks who aren't currently scheduled.
     * @param processorPrerequisites : Earliest moment in each processor when all data from currently scheduled parents
     *                                 is available.
     */
    protected TaskNodeState(int numUnscheduledLinks, int[] processorPrerequisites) {
        this.numUnscheduledLinks = numUnscheduledLinks;
        this.processorPrerequisites = processorPrerequisites;
    }

    /**
     * @return : Whether or not all of the task's parents have been scheduled.
     */
    @Override
    public boolean isFree() {
        // No unscheduled links means no unscheduled parents, thus task is free.
        return this.numUnscheduledLinks == 0;
    }

    /**
     * getProcessorPrerequisite will return garbage values if queried when the TaskNodeState is not free.
     * For speed reasons, ensuring that the method isn't being used improperly is not handled here.
     *
     * @param processorID : The ID of the processor on which the prerequisite location is being returned.
     * @return : The earliest moment that all required data sent by CURRENTLY SCHEDULED parents is available on
     *           a specified processor.
     */
    @Override
    public int getProcessorPrerequisite(int processorID) {
        return this.processorPrerequisites[processorID];
    }

    /**
     * Generic toString implementation.
     *
     * @return : A formatted string containing the data stored in this object's fields.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("TaskNodeState:\n")
                .append("numLinks: ").append(this.numUnscheduledLinks).append("\n")
                .append("Processor Prerequisites:\n");

        for (int i = 0; i < processorPrerequisites.length; i++) {
            builder.append(i).append(" - ").append(processorPrerequisites[i]).append("\n");
        }
        return builder.toString();
    }

}
