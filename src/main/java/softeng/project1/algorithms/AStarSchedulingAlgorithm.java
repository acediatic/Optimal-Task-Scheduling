package softeng.project1.algorithms;

import softeng.project1.algorithms.heuristics.HeuristicManager;
import softeng.project1.algorithms.heuristics.PriorityQueueHeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.ChangedScheduleState;
import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.processors.processor.Processor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AStarSchedulingAlgorithm implements SchedulingAlgorithm {

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;

    public AStarSchedulingAlgorithm(Schedule originalSchedule, HeuristicManager heuristicManager) {

        this.heuristicManager = heuristicManager;
        heuristicManager.add(originalSchedule);

        // TODO... Calculate a relevant initial size
        this.closedSchedules = new ConcurrentHashMap<>();
    }

    @Override
    public List<int[]> generateSchedule() {

        List<Schedule> fringeSchedules;
        AlgorithmStep step;

        // TODO... Find a better way to indicate finish than null
        while ((fringeSchedules = (step = heuristicManager.get()).takeStep()) != null) {

            for (Schedule fringeSchedule: fringeSchedules) {
                // Pruning already existing schedules
                if (!closedSchedules.containsKey(fringeSchedule.getHashKey())) {
                    closedSchedules.put(fringeSchedule.getHashKey(), fringeSchedule);
                    // TODO... pass these all at once to reduce number of priority queue orderings
                    heuristicManager.add(fringeSchedule);
                }
                // TODO... make sure our heuristic is consistent and thus we don't need to check h-values on map insert

            }
        }
        // Just calling ScheduleStateChange.rebuildSolutionPath() but via a few parent objects.
        return step.rebuildPath();
    }










}
