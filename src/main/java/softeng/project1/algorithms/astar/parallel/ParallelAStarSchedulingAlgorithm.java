package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
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
    private final Schedule originalSchedule;
    private final BlockingQueue<Runnable> queue;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads) {
        super(numThreads, numThreads, KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

        // TODO... give map a useful original size
        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new CopyOnWriteArrayList<>();
        this.originalSchedule = originalSchedule;
        this.queue = super.getQueue();
    }

    @Override
    public List<int[]> generateSchedule() {

        this.closedSchedules.put(originalSchedule.getHashKey(), originalSchedule);
        execute(heuristicManager.getAlgorithmStepFromSchedule(originalSchedule));

        try {
            if (this.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                return this.optimalSchedules.get(0);
            } else {
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {

        List<Schedule> fringeSchedules;
        AlgorithmStep algorithmStep = (AlgorithmStep) runnable;

        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            this.optimalSchedules.add(algorithmStep.rebuildPath());
            shutdown();
        } else {

            for (AlgorithmStep step : this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules))) {
                try {
                    execute(step);
                } catch (RejectedExecutionException e) {
                    // This is fine...
                }
            }
        }
    }


    @Override
    public List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> expandedSchedules) {
        List<Schedule> unexploredSchedules = new ArrayList<>();
        for (Schedule expandedSchedule : expandedSchedules) {

            if (!(this.closedSchedules.containsKey(expandedSchedule.getHashKey()) && this.closedSchedules.get(expandedSchedule.getHashKey()).deepEquals(expandedSchedule))) {
                this.closedSchedules.put(expandedSchedule.getHashKey(), expandedSchedule);
                unexploredSchedules.add(expandedSchedule);
            } else {
                // TODO... ensure that our heuristic is consistent so we don't have to do anything here
            }
        }

        return unexploredSchedules;
    }
}
