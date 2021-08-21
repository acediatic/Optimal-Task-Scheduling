package softeng.project1.graph;

import softeng.project1.graph.tasks.TaskNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * ScheduleStateChange represents the change that occurs to generate one partial schedule from another.
 * Data regarding the task that was inserted is stored and can be used to recursively generate the full set of task
 * inserts that occurred to eventually generate the fringe schedule state.
 *
 * As with any object that represents states, ScheduleStateChange should be considered immutable.
 */
public class ScheduleStateChange {

    // Immutable data fields.
    private final ScheduleStateChange previousChange; // The state change that created the state that this change
                                                      // started from.
    private final int insertedTaskNodeID; // The ID of the task whose insertion is represented by this state change.
    private final int processorID; // The ID of the processor the task was inserted into.
    private final int insertionTime; // The start location of the inserted task.
    private final int taskNodeWeight;

    /**
     * Generic constructor which takes and stores inputted state change data.
     *
     * @param previousChange : State change that describes the creation of the state that this state change started
     *                         from.
     * @param insertedTaskNodeID : ID of the task whose insertion is represented by this state change.
     * @param processorID : ID of the processor that the task was inserted into.
     * @param insertionTime : Start location of the inserted task.
     */
    public ScheduleStateChange(ScheduleStateChange previousChange, int insertedTaskNodeID, int processorID, int insertionTime, int taskNodeWeight) {
        this.previousChange = previousChange;
        this.insertedTaskNodeID = insertedTaskNodeID;
        this.processorID = processorID;
        this.insertionTime = insertionTime;
        this.taskNodeWeight = taskNodeWeight;
    }

    /**
     * Recursively generates the full set of inserted task locations, as a list of int arrays, that occurred to generate
     * the state this object represents the creation of.
     * Data is stored in the integer arrays following the following format:
     *  - solutionPathStep[0] => ID of the inserted task.
     *  - solutionPathStep[1] => ID of the processor the task was inserted into.
     *  - solutionPathStep[2] => Start location of the inserted task.
     *
     * The ordering of the list is equivalent to the order that the algorithm generated the schedule. This is relatively
     * meaningless so the list should be considered to be ordered arbitrarily.
     *
     * @return : An arbitrarily order list of arrays representing all task insertions that occurred in generating the
     *           ScheduleState that this object represents the creation of.
     */
    public List<int[]> rebuildSolutionPath() {
        // Own data
        int[] pathData = new int[]{insertedTaskNodeID, processorID, insertionTime, taskNodeWeight};

        // First step, occurs when creating step from original schedule state
        if (previousChange == null) {
            return new LinkedList<>(Arrays.asList(new int[][]{pathData}));
        } else {
            // Recursively build from previous steps
            List<int[]> returnList = previousChange.rebuildSolutionPath();
            returnList.add(pathData);
            return returnList;
        }
    }
}
