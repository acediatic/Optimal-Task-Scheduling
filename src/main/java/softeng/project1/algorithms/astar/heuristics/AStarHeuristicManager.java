package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;

public class AStarHeuristicManager implements HeuristicManager {
    private final int taskLengthsSum;
    private final short numberOfProcesses;
    private AlgorithmStep listScheduling;
    private int listSchedulingPriority;

    public AStarHeuristicManager(int taskLengthsSum, short numberOfProcesses, AlgorithmStep listScheduling) {
        this.taskLengthsSum = taskLengthsSum;
        this.numberOfProcesses = numberOfProcesses;
        this.listScheduling = listScheduling;
        this.listSchedulingPriority = listScheduling.getPriorityValue();
    }

    @Override
    public List<AlgorithmStep> getAlgorithmStepsFromSchedules(List<Schedule> newFringeSchedules) {
        List<AlgorithmStep> algoSteps = new ArrayList<>(newFringeSchedules.size());

        // Convert schedules to algorithm steps
        for (Schedule schedule : newFringeSchedules) {
            AlgorithmStep algoStep = getAlgoStep(schedule);
            if (algoStep != null) {
                algoSteps.add(algoStep);
            }
        }

        // add all to priority queue.
        return algoSteps;
    }

    @Override
    public AlgorithmStep getAlgorithmStepFromSchedule(Schedule newFringeSchedule) {
        return getAlgoStep(newFringeSchedule);
    }

    // TODO describe heuristic
    /*
     * f(s) = max{f_idle-time(s), f_bl(s), f_DRT(s)}
     */
    private int calculateHeuristicValue(Schedule schedule) {
        // Two Math.max calls because it only takes two inputs, and we need three
        return Math.max(Math.max(
                        (taskLengthsSum + schedule.getIdleTime()) / numberOfProcesses,      // f_idle_time
                        schedule.getMaxBottomLevel()),                                      // f_bl
                schedule.getMaxDataReadyTime());                                            // f_DRT
    }

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
