package softeng.project1.graph;

import softeng.project1.graph.processors.OriginalProcessorsState;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;
import softeng.project1.graph.tasks.TaskNodeState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Implementation of ScheduleState that specifically represents the state of the Schedule before any tasks are inserted
 * into any Processors.
 * By the nature of it's representation, OriginalScheduleState is effectively a singleton class.
 *
 * In order to reduce the algorithm's memory complexity OriginalScheduleState is the only ScheduleState which stores
 * all data needed to fully describe the schedule. Other implementations should instead store only a reference to the
 * OriginalScheduleState and pass any attempts to get data, not privately stored, down to it.
 *
 * OriginalScheduleState is also the only ScheduleState that should be directly instantiated. In order to ensure that
 * ScheduleStates stay immutable, all other ScheduleStates should be generated via the expand method only.
 * Just like all other ScheduleState objects OriginalScheduleState should be considered immutable.
 *
 * For speed reasons, some of the methods described by Schedule/ScheduleState have been shorted by using assumptions
 * about the Original state.
 */
public class OriginalScheduleState extends ScheduleState {

    private static final float LOAD_FACTOR = 100;

    private static final ScheduleStateChange ORIGINAL_STATE_CHANGE = null;
    private static final int ORIGINAL_MAX_BOTTOM_LEVEL = 0;
    private static final int ORIGINAL_MAX_DATA_READY_TIME = 0;

    private final short branchingFactor;

    /**
     * Public constructor for starting the tree of partial schedules which all grow from this original state.
     * This should be the ONLY public constructor available for any ScheduleState implementations. All others should
     * only be generated through the expand() method. This is done to ensure that all ScheduleStates are immutable and
     * all are part of the same tree with this class as the root node.
     * Generation of the Original Processor objects are handled within this constructor and thus do not need to be
     * inserted manually.
     *
     * @param taskNodes : A map of TaskID to Original Task states including ALL tasks that need to be scheduled.
     * @param freeTaskNodes : A map of TaskID to Original Task states including ONLY tasks that have no parents.
     * @param numProcessors : The number of processors that the tasks can be scheduled upon.
     */
    public OriginalScheduleState(Map<Short, TaskNode> taskNodes,
                                 Map<Short, TaskNode> freeTaskNodes,
                                 int numProcessors,
                                 short branchingFactor) {
        // Making these immutable, note that the underlying map can still be changed.
        super(
                // We could replace these with arrays in the original state, need to be maps in non-original states
                Collections.unmodifiableMap(taskNodes),
                Collections.unmodifiableMap(freeTaskNodes),
                new OriginalProcessorsState(numProcessors),
                ORIGINAL_STATE_CHANGE,
                ORIGINAL_MAX_BOTTOM_LEVEL,
                ORIGINAL_MAX_DATA_READY_TIME
        );
        this.branchingFactor = branchingFactor;
    }

    /**
     * Shorted method from Schedule.
     * The original schedule is effectively a singleton class so we need not bother checking the other schedule's data.
     * @param otherSchedule : The other Schedule object being equated to this one.
     * @return : Whether or not the given schedule is exactly the Original Schedule.
     */
    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        // Only ever equal to itself
        return this == otherSchedule;
    }

    /**
     * Implementation of the helper method from ScheduleState.
     * Previous change is set to null as no change exists before the Original state.
     *
     * @param freeTaskID : The ID of the task being inserted into the new schedule that the schedule state change
     *                     represents.
     * @param processorID : The ID of the processor that the task was inserted into.
     * @param insertLocation : The start location of the scheduled task.
     * @return : The top level StateChange object created from the generation of a state from the original state.
     */
    @Override
    protected ScheduleStateChange generateStateChange(int freeTaskID, int processorID, int insertLocation, int freeTaskWeight) {
        return new ScheduleStateChange(
                ORIGINAL_STATE_CHANGE, // No previous change
                freeTaskID,
                processorID,
                insertLocation,
                freeTaskWeight
        );
    }

    /**
     * Shorted helper method from ProcessorState.
     * @return : this.
     */
    @Override
    protected OriginalScheduleState getOriginalSchedule() {
        return this;
    }

    /**
     * Shorted helper method from ProcessorState.
     *
     * @param taskID : The ID of the task to return.
     * @return : The original state of the task with the given ID.
     */
    public TaskNode getTaskNode(short taskID) {
       return this.taskNodes.get(taskID);
    }

    @Override
    protected Map<Short, TaskNode> copyFreeNodesHook() {
        return new HashMap<>(this.branchingFactor, LOAD_FACTOR);
    }

    @Override
    protected Map<Short, TaskNode> copyTaskNodesHook() {
        return new HashMap<>(this.branchingFactor, LOAD_FACTOR);
    }
}
