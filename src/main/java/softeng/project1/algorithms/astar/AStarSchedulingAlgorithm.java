package softeng.project1.algorithms.astar;

import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AStarSchedulingAlgorithm implements SchedulingAlgorithm {

    protected final HeuristicManager heuristicManager;
    protected final Map<Processors, Schedule> closedSchedules;

    public AStarSchedulingAlgorithm(Schedule originalSchedule,
                                    HeuristicManager heuristicManager,
                                    Map<Processors, Schedule> closedSchedules) {

        this.heuristicManager = heuristicManager;
        heuristicManager.add(originalSchedule);

        // TODO... Calculate a relevant initial size
        this.closedSchedules = closedSchedules;
    }

    @Override
    public List<int[]> generateSchedule() {

        List<Schedule> fringeSchedules;
        AlgorithmStep step;

        // TODO... Find a better way to indicate finish than null
        while ((fringeSchedules = (step = this.heuristicManager.get()).takeStep()) != null) {
            this.heuristicManager.addAll(pruneExpandedSchedulesAndAddToMap(fringeSchedules, this.closedSchedules));
        }
        // Just calling ScheduleStateChange.rebuildSolutionPath() but via a few parent objects.
        return step.rebuildPath();
    }

    protected List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> expandedSchedules,
                                                    Map<Processors, Schedule> closedSchedules) {
        List<Schedule> unexploredSchedules = new ArrayList<>();
        for (Schedule expandedSchedule: expandedSchedules) {
            if (!closedSchedules.containsKey(expandedSchedule.getHashKey())) {
                closedSchedules.put(expandedSchedule.getHashKey(), expandedSchedule);
                unexploredSchedules.add(expandedSchedule);
            } else {
                // TODO... ensure that our heuristic is consistent so we don't have to do anything here
            }
        }

        return unexploredSchedules;
    }










}
