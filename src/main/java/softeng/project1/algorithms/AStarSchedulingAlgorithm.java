package softeng.project1.algorithms;

import softeng.project1.algorithms.heuristics.HeuristicManager;
import softeng.project1.algorithms.heuristics.PriorityQueueHeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.ScheduleState;

import java.util.Hashtable;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class AStarSchedulingAlgorithm implements SchedulingAlgorithm{

    private final Schedule originalSchedule;
    private final HeuristicManager heuristicManager;
    private final Map<Long, ScheduleState> closedSchedules;

    public AStarSchedulingAlgorithm(Schedule originalSchedule) {
        this.originalSchedule = originalSchedule;
        this.heuristicManager = new PriorityQueueHeuristicManager();
        this.closedSchedules = new ConcurrentHashMap<>();
    }










}
