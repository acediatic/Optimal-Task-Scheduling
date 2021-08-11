package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.BlockingQueueHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ParallelAStarSchedulingAlgorithm extends ThreadPoolExecutor implements AStarSchedulingAlgorithm {

    private static final long KEEP_ALIVE_TIME_MILLISECONDS = 0;

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;
    private final List<List<int[]>> optimalSchedules;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            BlockingQueueHeuristicManager heuristicManager,
                                            int numThreads) {
        super(numThreads, numThreads, KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS, heuristicManager);

        // TODO... give map a useful original size
        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new ArrayList<>();
    }

    @Override
    public List<int[]> generateSchedule() {

        Runnable algorithmStep;

        while (!this.isShutdown()) {
            // Wait for shutdown
        }
        // TODO... do better than this.
        return this.optimalSchedules.get(0);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {

        List<Schedule> fringeSchedules;
        AlgorithmStep algorithmStep = (AlgorithmStep) runnable;

        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            this.optimalSchedules.add(algorithmStep.rebuildPath());
            shutdown();
        } else {
            this.heuristicManager.addAll(pruneExpandedSchedulesAndAddToMap(fringeSchedules));
        }
    }


    @Override
    public List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> expandedSchedules) {
        List<Schedule> unexploredSchedules = new ArrayList<>();
        for (Schedule expandedSchedule: expandedSchedules) {
            if (!this.closedSchedules.containsKey(expandedSchedule.getHashKey())) {
                this.closedSchedules.put(expandedSchedule.getHashKey(), expandedSchedule);
                unexploredSchedules.add(expandedSchedule);
            } else {
                // TODO... ensure that our heuristic is consistent so we don't have to do anything here
            }
        }

        return unexploredSchedules;
    }
}
