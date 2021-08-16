package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;
import java.util.PriorityQueue;

public class PriorityQueueHeuristicManager implements HeuristicManager {
    private PriorityQueue<AlgorithmStep> priorityQueue = new PriorityQueue<>();
    private short taskLengthsSum;
    private short numberOfProcesses;

    public PriorityQueueHeuristicManager(short taskLengthsSum, short numberOfProcesses) {
        this.taskLengthsSum = taskLengthsSum;
        this.numberOfProcesses = numberOfProcesses;
    }

    @Override
    public AlgorithmStep get() {
        return null;
    }

    @Override
    public void addAll(List<Schedule> newFringeSchedules) {
        for (Schedule schedule : newFringeSchedules) {
            add(schedule);
        }
    }

    @Override
    public void add(Schedule fringeSchedule) {
        // TODO pruning, check if should be added

        // should be added, so calculate heuristic, wrap in algorithm step and add.
        AlgorithmStep algoStep = new AlgorithmStep(calculateHeuristicValue(fringeSchedule), fringeSchedule);
        priorityQueue.add(algoStep);
    }

    // TODO describe heuristic
    /*
     * f(s) = max{f_idle-time(s), f_bl(s), f_DRT(s)}
     */
    private int calculateHeuristicValue(Schedule schedule) {
        // Two Math.max calls because it only takes two inputs and we need three
        return Math.max(Math.max(
                        (taskLengthsSum + schedule.getIdleTime()) / numberOfProcesses,      // f_idle_time
                        schedule.getMaxBottomLevel()),                                      // f_bl
                schedule.getMaxDataReadyTime());                                            // f_DRT
    }
}
