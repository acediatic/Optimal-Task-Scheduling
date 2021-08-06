package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class ScheduleState implements Schedule{
    private final OriginalSchedule originalSchedule;
    private final ScheduleStateChange change;
    private final Map<Integer, TaskNode> taskNodes;
    private final Map<Integer, TaskNode> freeNodes;
    private final Processors processors;

    private int maxBottomLevel = 0;
    private int idleTime = 0;

    protected ScheduleState(
            OriginalSchedule originalSchedule,
            ScheduleStateChange change,
            Map<Integer, TaskNode> taskNodes,
            Map<Integer, TaskNode> freeNodes,
            Processors processors) {
        this.originalSchedule = originalSchedule;
        this.change = change;
        this.taskNodes = taskNodes;
        this.freeNodes = freeNodes;
        this.processors = processors;
    }


    @Override
    public long getHashKey() {
        return this.processors.murmurHash();
    }

    @Override
    public boolean deepEquals(Schedule otherSchedule) {
        try {
            return ((ScheduleState) otherSchedule).processors.deepEquals(this.processors);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int getMaxDataReadyTime() {
        return 0; // TODO...
    }

    // TODO
    // calculate bottom level, idle time increment for each added task here too.
    // Data Transfer time from ProcessorPrerequisite of each TaskNodeState.
    public List<ScheduleStateChange> expand() {

        return null;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public int getMaxBottomLevel() {
        return maxBottomLevel;
    }
}
