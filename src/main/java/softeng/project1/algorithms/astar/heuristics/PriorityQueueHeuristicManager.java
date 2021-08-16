package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.algorithms.astar.AlgorithmStep;
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

    }

    @Override
    public void add(Schedule fringeSchedule) {
        // check if should be added


        // should be added, so calculate heuristic, wrap in algorithm step and add.
    }

    // TODO
    /*
     * f(s) = max{f_idle-time(s), f_bl(s), f_DRT(s)}
     */
    private int calculateHeuristicValue(Schedule schedule) {
        int f_idle_time = (taskLengthsSum + schedule.getIdleTime())/numberOfProcesses;

        int temp_max = Math.max(f_idle_time, schedule.getMaxBottomLevel());
        temp_max = Math.max(temp_max, schedule.getMaxDataReadyTime());

        return temp_max;
    }
}
