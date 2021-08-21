package softeng.project1.algorithms.astar.sequential;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SequentialAStarSchedulingAlgorithm implements AStarSchedulingAlgorithm {

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;
    private final PriorityQueue<AlgorithmStep> priorityQueue;
    private final Schedule originalSchedule;

    private final AtomicInteger numSchedulesChecked;

    public SequentialAStarSchedulingAlgorithm(Schedule originalSchedule,
                                              HeuristicManager heuristicManager,
                                              Map<Processors, Schedule> closedSchedules,
                                              PriorityQueue<AlgorithmStep> priorityQueue) {

        this.originalSchedule = originalSchedule;
        this.heuristicManager = heuristicManager;

        // TODO... Calculate a relevant initial size
        this.closedSchedules = closedSchedules;
        this.priorityQueue = priorityQueue;
        this.numSchedulesChecked = new AtomicInteger(0);
    }

    @Override
    public List<int[]> generateSchedule() {

        this.priorityQueue.add(this.heuristicManager.getAlgorithmStepFromSchedule(originalSchedule));

        List<Schedule> fringeSchedules;
        AlgorithmStep step;

        // TODO... Find a better way to indicate finish than null
        while ((fringeSchedules = (step = this.priorityQueue.poll()).takeStep()) != null) {
            this.numSchedulesChecked.incrementAndGet();
            this.priorityQueue.addAll(this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules)));
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

    @Override
    public int addReporterTask() {

        return this.numSchedulesChecked.intValue();

    }

}
