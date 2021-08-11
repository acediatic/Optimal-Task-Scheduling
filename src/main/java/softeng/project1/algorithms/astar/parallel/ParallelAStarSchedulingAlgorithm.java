package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ParallelAStarSchedulingAlgorithm extends AStarSchedulingAlgorithm {

    private final AStarParallelThreadPoolExecutor executorService;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads) {
        // TODO... give map a useful original size
        super(originalSchedule, heuristicManager, new ConcurrentHashMap<>());
        // TODO... stop this from referencing itself.
        this.executorService = new AStarParallelThreadPoolExecutor(numThreads, heuristicManager, this);

    }

    @Override
    public List<int[]> generateSchedule() {

        Runnable algorithmStep;

        while (!this.executorService.isShutdown()) {
            // Wait for shutdown
        }

        return this.executorService.getOptimalSolution();


    }

    public void putFringeSchedules(List<Schedule> fringeSchedules) {
        this.heuristicManager.addAll(pruneExpandedSchedulesAndAddToMap(fringeSchedules, this.closedSchedules));
    }


}
