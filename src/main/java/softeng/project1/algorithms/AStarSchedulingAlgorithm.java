package softeng.project1.algorithms;

import softeng.project1.algorithms.heuristics.HeuristicManager;
import softeng.project1.algorithms.heuristics.PriorityQueueHeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.ChangedScheduleState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AStarSchedulingAlgorithm implements SchedulingAlgorithm{

    private final Schedule originalSchedule;
    private final HeuristicManager heuristicManager;
    private final Map<Long, ChangedScheduleState> closedSchedules;

    public AStarSchedulingAlgorithm(Schedule originalSchedule) {
        short taskLengthsSum = 0; // initialise to value of graph.


        this.originalSchedule = originalSchedule;
        this.heuristicManager = new PriorityQueueHeuristicManager(taskLengthsSum);
        this.closedSchedules = new ConcurrentHashMap<>();
    }










}
