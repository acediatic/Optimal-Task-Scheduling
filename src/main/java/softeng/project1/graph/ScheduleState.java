package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;

import java.util.HashMap;

public class ScheduleState {
    private OriginalSchedule originalSchedule;
    private ScheduleStateChange change;
    private HashMap<Integer, TaskNode> taskNodes;
    private HashMap<Integer, TaskNode> freeNodes;
    private Processors processors;

    private int maxBottomLevel = 0;

    public List<ScheduleStateChange>() {

    }
}
