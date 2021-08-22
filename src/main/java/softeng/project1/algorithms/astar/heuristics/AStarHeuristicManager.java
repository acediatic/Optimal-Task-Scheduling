package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * The AStarHeuristicManager is in charge of managing the heuristic value generation for the A*
 * algorithm. It has methods which take in schedules, and return them as algorithm steps, within
 * which are their calculated heuristic values.
 */
public class AStarHeuristicManager implements HeuristicManager {
    private final int taskLengthsSum;
    private final short numberOfProcesses;
    int listSchedulingPriority;

    public AStarHeuristicManager(int taskLengthsSum, short numberOfProcesses, AlgorithmStep listScheduling) {
        this.taskLengthsSum = taskLengthsSum;
        this.numberOfProcesses = numberOfProcesses;
        // The list scheduling priority is used to prune schedules.
        this.listSchedulingPriority = listScheduling.getPriorityValue();
    }

    /**
     * This method generates a list of Algorithm steps from the input schedules, adding their heuristic
     * values. It will perform pruning first, so the input size >= output size.
     *
     * @param newFringeSchedules the schedules to convert to algorithm steps
     * @return a list of algorithm steps, after pruning
     */
    @Override
    public List<AlgorithmStep> getAlgorithmStepsFromSchedules(List<Schedule> newFringeSchedules) {
        List<AlgorithmStep> algoSteps = new ArrayList<>(newFringeSchedules.size());

        // Convert schedules to algorithm steps
        for (Schedule schedule : newFringeSchedules) {
            AlgorithmStep algoStep = getAlgoStep(schedule);
            // null indicates this step has been pruned
            if (algoStep != null) {
                algoSteps.add(algoStep);
            }
        }

        // return so they can be added to the priority queue.
        return algoSteps;
    }

    /**
     * Returns an algorithm step for a single schedule, if it isn't pruned
     *
     * @param newFringeSchedule the schedule to convert
     * @return the corresponding algorithm step, or null if it has been pruned.
     */
    @Override
    public AlgorithmStep getAlgorithmStepFromSchedule(Schedule newFringeSchedule) {
        return getAlgoStep(newFringeSchedule);
    }

    /*
     * The heuristic value chosen here is that described in the paper
     * [here](https://www.sciencedirect.com/science/article/abs/pii/S0305054813002542?via%3Dihub)
     *
     * The heuristic is the maximum of the f-idle time, f-bl, and f-drt. For more information, please
     * see Wiki.
     *
     * f(s) = max{f_idle-time(s), f_bl(s), f_DRT(s)}
     */
    int calculateHeuristicValue(Schedule schedule) {
        // Two Math.max calls because it only takes two inputs, and we need three
        return Math.max(Math.max(
                        (taskLengthsSum + schedule.getIdleTime()) / numberOfProcesses,      // f_idle_time
                        schedule.getMaxBottomLevel()),                                      // f_bl
                schedule.getMaxDataReadyTime());                                            // f_DRT
    }

    /**
     * A utility method used by multiple others in this class.
     * It checks if the input schedule should be pruned; that is, it's heuristic value is less than or equal
     * to that of the list scheduler, which we know to be an upper bound on the optimal. If it is, it is pruned
     * and null is returned. If it has a heuristic value less than the list scheduler, the algorithm step object
     * is created and returned.
     *
     * @param fringeSchedule the schedule to convert
     * @return the corresponding algorithm step, or null if it has been pruned.
     */
    public AlgorithmStep getAlgoStep(Schedule fringeSchedule) {
        AlgorithmStep algorithmStep = new AlgorithmStep(calculateHeuristicValue(fringeSchedule), fringeSchedule);
        if (algorithmStep.getPriorityValue() >= listSchedulingPriority) {
            // Don't add to schedule, already have better/equivalent in there.
            return null;
        } else {
            return algorithmStep;
        }
    }
}
