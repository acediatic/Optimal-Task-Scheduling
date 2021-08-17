package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

public class HeuristicHelper {
    private final int taskLengthsSum;
    private final short numberOfProcesses;

    public HeuristicHelper(int taskLengthsSum, short numberOfProcesses) {
        this.taskLengthsSum = taskLengthsSum;
        this.numberOfProcesses = numberOfProcesses;
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
        return new AlgorithmStep(calculateHeuristicValue(fringeSchedule), fringeSchedule);
    }
}
