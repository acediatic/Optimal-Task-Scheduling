package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class ChangedScheduleState extends ScheduleState{

    private final OriginalScheduleState originalScheduleState;
    private final ScheduleStateChange change;

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

    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        try {
            return ((ChangedScheduleState) otherSchedule).processors.equals(this.processors);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    protected OriginalScheduleState getOriginalSchedule() {
        return this.originalScheduleState;
    }

    @Override
    protected ScheduleStateChange generateStateChange(TaskNode freeTask, int processorID, int insertLocation) {
        return new ScheduleStateChange(
                this.change,
                freeTask,
                processorID,
                insertLocation
        );
    }

    protected TaskNode getTaskNode(int taskID) {
        TaskNode returnNode;
        if ((returnNode = this.taskNodes.get(taskID)) != null) {
            return returnNode;
        } else {
            return this.originalScheduleState.getTaskNode(taskID);
        }
    }

}
