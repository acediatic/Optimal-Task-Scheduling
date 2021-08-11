package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AStarParallelThreadPoolExecutor extends ThreadPoolExecutor {

    private final ParallelAStarSchedulingAlgorithm algorithm;
    private final List<List<int[]>> optimalSolutions;

    public AStarParallelThreadPoolExecutor(int corePoolSize,
                                           HeuristicManager heuristicManager,
                                           ParallelAStarSchedulingAlgorithm algorithm) {
        super(corePoolSize, corePoolSize, keepAliveTime, unit, heuristicManager);
        this.algorithm = algorithm;
        this.optimalSolutions = new ArrayList<>();
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {

        List<Schedule> fringeSchedules;
        AlgorithmStep algorithmStep = (AlgorithmStep) runnable;

        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            this.optimalSolutions.add(algorithmStep.rebuildPath());
            shutdown();
        } else {
            // This is pretty cursed tbh
            this.algorithm.putFringeSchedules(fringeSchedules);
        }

    }

    public List<int[]> getOptimalSolution() {
        // TODO... actually check which of these is the best
        return this.optimalSolutions.get(0);
    }
}
