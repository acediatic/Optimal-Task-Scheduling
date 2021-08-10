package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.TaskNode;

import java.util.Map;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Implementation of ScheduleState that specifically represents partial schedules where at least one task has been
 * scheduled into a processor.
 *
 * In order to reduce the memory usage of A*, ChangedScheduleState attempts to store as little data as possible, and
 * effectively duplicate ChangedScheduleStates should be pruned.
 * Reducing data storage is done by outsourcing most data to the Original State and only ever storing data that is both
 * CHANGED and RELEVANT.
 * If data is needed regarding the partial schedule and it hasn't been changed then ChangedScheduleState will simply
 * pass the data request down to it's stored OriginalScheduleState.
 *
 * As with all other ScheduleState implementations, ChangedScheduleState is effectively immutable so care should be
 * taken not to directly expose any of the stored data structures.
 */
public class ChangedScheduleState extends ScheduleState{

    // Immutable state fields
    private final OriginalScheduleState originalScheduleState; // The original schedule state that stores all data
    private final ScheduleStateChange change; // The state change that occurred in the creation of this object

    /**
     * Protected constructor for use in the expand method only.
     *
     * @param originalScheduleState : The reference to the original state where all data is stored.
     * @param change : The state change that occurred during the creation of this object.
     * @param taskNodes : The task states which are DIFFERENT to the original state of their task.
     * @param freeNodes : The task states which currently have no unscheduled parents.
     * @param processors : The Processors object which stores the current state of the processors.
     * @param maxBottomLevel : The longest critical path that stems from any scheduled task.
     * @param maxDataReadyTime : The earliest moment that all data from all scheduled tasks are available to all
     *                           children on all processors.
     */
    protected ChangedScheduleState(
            OriginalScheduleState originalScheduleState,
            ScheduleStateChange change,
            Map<Integer, TaskNode> taskNodes,
            Map<Integer, TaskNode> freeNodes,
            Processors processors,
            int maxBottomLevel,
            int maxDataReadyTime) {
        super(taskNodes, freeNodes, processors, maxBottomLevel, maxDataReadyTime);
        this.originalScheduleState = originalScheduleState;
        this.change = change;
    }

    /**
     * Implementation of the method from Schedule.
     * @param otherSchedule : The other Schedule object being equated to this one.
     * @return : Whether or not the given Schedule has an equivalent Processors state to this object.
     */
    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        try {
            return ((ChangedScheduleState) otherSchedule).processors.equals(this.processors);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Implementation of helper method from ScheduleState.
     * @return : The Original Schedule State that stores all original data.
     */
    @Override
    protected OriginalScheduleState getOriginalSchedule() {
        return this.originalScheduleState;
    }

    /**
     * Implementation of helper method from ScheduleState.
     * @param freeTaskID : The ID of the task being inserted into the new schedule that the schedule state change
     *                     represents.
     * @param processorID : The ID of the processor that the task was inserted into.
     * @param insertLocation : The start location of the scheduled task.
     * @return : The state change that occurs when a specific task is inserted into the current state of the Processors
     */
    @Override
    protected ScheduleStateChange generateStateChange(int freeTaskID, int processorID, int insertLocation) {
        return new ScheduleStateChange(
                this.change,
                freeTaskID,
                processorID,
                insertLocation
        );
    }

    /**
     * Implementation of helper method from ScheduleState.
     * @param taskID : The ID of the task to return.
     * @return : Returns the current state of the task with the given ID.
     */
    protected TaskNode getTaskNode(int taskID) {
        TaskNode returnNode;
        // Has the task been changed?
        if ((returnNode = this.taskNodes.get(taskID)) != null) {
            // Return changed task state
            return returnNode;
        } else {
            // Return original task state
            return this.originalScheduleState.getTaskNode(taskID);
        }
    }

}
