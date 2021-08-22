package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;

/**
 * An interface defining how the heuristic manager, a class in charge of generating algorithm steps from schedules
 * should behave.
 */
public interface HeuristicManager {

    /**
     * This method generates a list of Algorithm steps from the input schedules, adding their heuristic
     * values. It will perform pruning first, so the input size >= output size.
     *
     * @param newFringeSchedules the schedules to convert to algorithm steps
     * @return a list of algorithm steps, after pruning
     */
    List<AlgorithmStep> getAlgorithmStepsFromSchedules(List<Schedule> newFringeSchedules);

    /**
     * Returns an algorithm step for a single schedule, if it isn't pruned
     *
     * @param newFringeSchedule the schedule to convert
     * @return the corresponding algorithm step, or null if it has been pruned.
     */
    AlgorithmStep getAlgorithmStepFromSchedule(Schedule newFringeSchedule);

}
