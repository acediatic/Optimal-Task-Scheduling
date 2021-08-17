package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class AlgorithmPriorityBlockingQueue extends PriorityBlockingQueue<AlgorithmStep> implements HeuristicManager {
    private final HeuristicHelper heuristicHelper;

    public AlgorithmPriorityBlockingQueue(int taskLengthsSum, short numberOfProcesses) {
        this.heuristicHelper = new HeuristicHelper(taskLengthsSum, numberOfProcesses);
    }

    @Override
    // TODO null check?
    public AlgorithmStep getAlgorithmStepQueueHead() {
        return this.poll();
    }

    @Override
    public void addAllSchedules(List<Schedule> newFringeSchedules) {
        List<AlgorithmStep> algoSteps = new ArrayList<>(newFringeSchedules.size());

        // Convert schedules to algorithm steps
        for (Schedule schedule : newFringeSchedules) {
            algoSteps.add(heuristicHelper.getAlgoStep(schedule));
        }

        // add all to priority queue.
        this.addAll(algoSteps);
    }

    @Override
    public void addSchedule(Schedule fringeSchedule) {
        // TODO pruning, check if should be added
        // should be added, so calculate heuristic, wrap in algorithm step and add.
        this.add(heuristicHelper.getAlgoStep(fringeSchedule));
    }



}