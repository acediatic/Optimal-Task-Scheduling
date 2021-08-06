package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.TaskNode;

import java.util.HashMap;
import java.util.List;

public class ScheduleState {
    private OriginalSchedule originalSchedule;
    private ScheduleStateChange change;
    private HashMap<Integer, TaskNode> taskNodes;
    private HashMap<Integer, TaskNode> freeNodes;
    private Processors processors;
    private int maxBottomLevel = 0;
    private int idleTime = 0;

    // TODO
    // calculate bottom level, idle time increment for each added task here too.
    // Data Transfer time from ProcessorPrerequisite of each TaskNodeState.
    public List<ScheduleStateChange> expand() {

        return null;
    }
}
