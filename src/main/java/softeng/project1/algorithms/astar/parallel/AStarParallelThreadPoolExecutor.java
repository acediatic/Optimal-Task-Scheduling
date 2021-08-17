package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.heuristics.BlockingQueueHeuristicManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AStarParallelThreadPoolExecutor extends ThreadPoolExecutor {

    private final ParallelAStarSchedulingAlgorithm algorithm;
    private final List<List<int[]>> optimalSolutions;

    public AStarParallelThreadPoolExecutor(int corePoolSize,
                                           BlockingQueueHeuristicManager heuristicManager,
                                           ParallelAStarSchedulingAlgorithm algorithm) {
        super(corePoolSize, corePoolSize, 0, TimeUnit.MILLISECONDS, heuristicManager);
        this.algorithm = algorithm;
        this.optimalSolutions = new ArrayList<>();
    }



    public List<int[]> getOptimalSolution() {
        // TODO... actually check which of these is the best
        return this.optimalSolutions.get(0);
    }
}
