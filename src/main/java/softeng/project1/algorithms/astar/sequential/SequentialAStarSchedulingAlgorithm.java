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

/**
 * This class is responsible for managing the sequential execution of the search for the optimal schedule.
 * It uses a queue to prioritise the exploration of schedules, returning when the optimal is found.
 */
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
                                              PriorityQueue<AlgorithmStep> priorityQueue,
                                              AlgorithmStep listSchedule) {

        this.originalSchedule = originalSchedule;
        this.heuristicManager = heuristicManager;
        this.closedSchedules = closedSchedules;
        this.priorityQueue = priorityQueue;
        this.state = AlgorithmState.WAITING;
        this.priorityQueue.add(listSchedule);
    }

    /**
     * Generate schedule is the main method for the scheduling algorithm.
     * It is used to turn the provided original schedule into the optimal schedule.
     * This is done by repeatedly expanding schedules, and placing them in a priority
     * queue till the optimal is found, at which case this method returns
     *
     * @return List<int [ ]> generatedSchedule, the optiaml scheduling for the given original schedule state.
     */
    @Override
    public List<int[]> generateSchedule() {
        this.state = AlgorithmState.ACTIVE;
        // initially place our initial schedule in the priority queue, so we can pop in a loop.
        this.priorityQueue.add(this.heuristicManager.getAlgorithmStepFromSchedule(originalSchedule));

        List<Schedule> fringeSchedules;
        AlgorithmStep step;

        // returning null indicates that the algorithm has found the optimal solution, as there is nothing left for it
        // to expand (no free nodes left).
        while ((fringeSchedules = (step = this.priorityQueue.poll()).takeStep()) != null) {
            this.priorityQueue.addAll(this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules)));
        }
        // Just calling ScheduleStateChange.rebuildSolutionPath() but via a few parent objects.
        this.bestSchedule = step;
        // notify the GUI that the algorithm has found the optimal
        this.state = AlgorithmState.FINISHED;
        return step.rebuildPath();
    }

    /**
     * This prunes the expanded schedules we've already explored, and adds those that
     * haven't been seen before to the map of now seen schedules. This uses a hash,
     * where collisions are resolved by using a deep equals check. The consistency
     * of our heuristic allows us to do nothing if we have already explored the schedule.
     *
     * @param expandedSchedules the schedules to prune and add
     * @return the list of as yet unexplored schedules that should now be explored
     */
    @Override
    public List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> expandedSchedules) {
        List<Schedule> unexploredSchedules = new ArrayList<>();
        for (Schedule expandedSchedule : expandedSchedules) {
            if (!this.closedSchedules.containsKey(expandedSchedule.getHashKey())) {
                this.closedSchedules.put(expandedSchedule.getHashKey(), expandedSchedule);
                unexploredSchedules.add(expandedSchedule);
            }
            // As our heuristic is consistent, we do not have to do anything here, and can ignore the schedule.
        }

        return unexploredSchedules;
    }

    /**
     * A helper method to transfer data from the running algorithm to the GUI.
     *
     * @return GuiData, a data object storing information needed by the GUI.
     */
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
