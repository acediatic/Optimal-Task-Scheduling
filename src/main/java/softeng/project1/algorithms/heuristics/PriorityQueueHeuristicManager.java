package softeng.project1.algorithms.heuristics;

import softeng.project1.algorithms.AlgorithmStep;
import softeng.project1.graph.Schedule;

import java.util.PriorityQueue;

public class PriorityQueueHeuristicManager implements HeuristicManager {
    private PriorityQueue<AlgorithmStep> priorityQueue = new PriorityQueue<>();
    private short taskLengthsSum;

    public PriorityQueueHeuristicManager(short taskLengthsSum) {
        this.taskLengthsSum = taskLengthsSum;
    }

    @Override
    public AlgorithmStep get() {
        return null;
    }

    @Override
    public void add(Schedule fringeSchedule) {
        // check if should be added


        // should be added, so calculate heuristic, wrap in algorithm step and add.
    }

    // TODO
    private int calculateHeuristicValue(Schedule schedule) {
        return 0;
    }
}
