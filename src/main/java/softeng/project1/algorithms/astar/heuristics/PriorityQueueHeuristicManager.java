package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PriorityQueueHeuristicManager implements HeuristicManager {
    private final PriorityQueue<AlgorithmStep> priorityQueue;
    private final HeuristicHelper heuristicHelper;

    public PriorityQueueHeuristicManager(int taskLengthsSum, short numberOfProcesses) {
        this.priorityQueue = new PriorityQueue<>();
        this.heuristicHelper = new HeuristicHelper(taskLengthsSum, numberOfProcesses);
    }

    @Override
    // TODO null check?
    public AlgorithmStep getAlgorithmStepQueueHead() {
        return priorityQueue.poll();
    }

    @Override
    public void addAllSchedules(List<Schedule> newFringeSchedules) {
        List<AlgorithmStep> algoSteps = new ArrayList<>(newFringeSchedules.size());

        // Convert schedules to algorithm steps
        for (Schedule schedule : newFringeSchedules) {
            algoSteps.add(heuristicHelper.getAlgoStep(schedule));
        }

        // add all to priority queue.
        priorityQueue.addAll(algoSteps);
    }

    @Override
    public void addSchedule(Schedule fringeSchedule) {
        // TODO pruning, check if should be added
        // should be added, so calculate heuristic, wrap in algorithm step and add.
        priorityQueue.add(heuristicHelper.getAlgoStep(fringeSchedule));
    }



}
