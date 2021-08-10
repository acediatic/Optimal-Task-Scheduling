package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelAStarSchedulingAlgorithm extends AStarSchedulingAlgorithm {

    private final ExecutorService executor;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads) {
        super(originalSchedule, heuristicManager);
        this.executor = Executors.newFixedThreadPool(numThreads);

    }


}
