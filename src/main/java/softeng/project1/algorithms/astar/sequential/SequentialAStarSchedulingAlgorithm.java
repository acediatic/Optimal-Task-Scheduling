package softeng.project1.algorithms.astar.sequential;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequentialAStarSchedulingAlgorithm implements AStarSchedulingAlgorithm {

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;

    public SequentialAStarSchedulingAlgorithm(Schedule originalSchedule,
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
            this.heuristicManager.addAll(pruneExpandedSchedulesAndAddToMap(fringeSchedules));
        }
        // Just calling ScheduleStateChange.rebuildSolutionPath() but via a few parent objects.
        return step.rebuildPath();
    }

    @Override
    public List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> expandedSchedules) {
        List<Schedule> unexploredSchedules = new ArrayList<>();
        for (Schedule expandedSchedule: expandedSchedules) {
            if (!this.closedSchedules.containsKey(expandedSchedule.getHashKey())) {
                this.closedSchedules.put(expandedSchedule.getHashKey(), expandedSchedule);
                unexploredSchedules.add(expandedSchedule);
            } else {
                // TODO... ensure that our heuristic is consistent so we don't have to do anything here
            }
        }

        return unexploredSchedules;
    }

}
