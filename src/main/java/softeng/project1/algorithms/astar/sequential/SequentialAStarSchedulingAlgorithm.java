package softeng.project1.algorithms.astar.sequential;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;
import softeng.project1.gui.GuiData;

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

    private AlgorithmState state;
    private AlgorithmStep bestSchedule;

    public SequentialAStarSchedulingAlgorithm(Schedule originalSchedule,
                                              HeuristicManager heuristicManager,
                                              Map<Processors, Schedule> closedSchedules,
                                              PriorityQueue<AlgorithmStep> priorityQueue) {

        this.originalSchedule = originalSchedule;
        this.heuristicManager = heuristicManager;

        // TODO... Calculate a relevant initial size
        this.closedSchedules = closedSchedules;
        this.priorityQueue = priorityQueue;
        this.state = AlgorithmState.WAITING;
    }

    @Override
    public List<int[]> generateSchedule() {
        this.state = AlgorithmState.ACTIVE;
        this.priorityQueue.add(this.heuristicManager.getAlgorithmStepFromSchedule(originalSchedule));

        List<Schedule> fringeSchedules;
        AlgorithmStep step;

        // TODO... Find a better way to indicate finish than null
        while ((fringeSchedules = (step = this.priorityQueue.poll()).takeStep()) != null) {
            this.priorityQueue.addAll(this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules)));
        }
        // Just calling ScheduleStateChange.rebuildSolutionPath() but via a few parent objects.
        this.bestSchedule = step;
        this.state = AlgorithmState.FINISHED;
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
    public GuiData getGuiData() {

        if (this.state == AlgorithmState.WAITING) {
            return null;
        } else if (this.state == AlgorithmState.ACTIVE) {
            return new GuiData(
                    this.priorityQueue.peek(),
                    this.state,
                    this.closedSchedules.size()
            );
        } else if (this.state == AlgorithmState.FINISHED) {
            return new GuiData(
                    this.bestSchedule,
                    this.state,
                    this.closedSchedules.size()
            );
        } else {
            throw new RuntimeException("Case not setup in getGuiData");
        }
    }
}
