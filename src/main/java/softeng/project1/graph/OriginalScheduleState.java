package softeng.project1.graph;

import softeng.project1.graph.processors.OriginalProcessorsState;
import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class OriginalScheduleState extends ScheduleState {

    private static final int ORIGINAL_MAX_BOTTOM_LEVEL = 0;
    private static final int ORIGINAL_MAX_DATA_READY_TIME = 0;

    /**
     * TODO...
     */
    public OriginalScheduleState(Map<Integer, OriginalTaskNodeState> taskNodes, Map<Integer, OriginalTaskNodeState> freeTaskNodes, int numProcessors) {
        // Making these immutable, note that the underlying map can still be changed.
        super(
                Collections.unmodifiableMap(taskNodes),
                Collections.unmodifiableMap(freeTaskNodes),
                new OriginalProcessorsState(numProcessors),
                ORIGINAL_MAX_BOTTOM_LEVEL,
                ORIGINAL_MAX_DATA_READY_TIME
        );
    }


    @Override
    protected OriginalScheduleState getOriginalSchedule() {
        return this;
    }

    @Override
    protected ScheduleStateChange generateStateChange(TaskNode freeTask, int processorID, int insertLocation) {
        return new ScheduleStateChange(
                null, // No previous change
                freeTask,
                processorID,
                insertLocation
        );
    }

    protected TaskNode getTaskNode(int taskID) {
       return this.taskNodes.get(taskID);
    }

    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        // Only ever equal to itself
        return this == otherSchedule;
    }
}
