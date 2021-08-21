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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelAStarSchedulingAlgorithm extends ThreadPoolExecutor implements AStarSchedulingAlgorithm {

    private static final long KEEP_ALIVE_TIME_MILLISECONDS = 0;

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;
    private final List<List<int[]>> optimalSchedules;
    private final Schedule originalSchedule;
    private final AlgorithmStep listSchedule;
    private final AtomicLong atomicLong;
    private final BlockingQueue<Runnable> queue;
    private final AtomicInteger numSchedulesChecked;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads,
                                            AlgorithmStep listSchedule) {
        super(numThreads, numThreads, KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

        // TODO... give map a useful original size
        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new CopyOnWriteArrayList<>();
        this.originalSchedule = originalSchedule;
        this.listSchedule = listSchedule;
        this.atomicLong = new AtomicLong();
        this.queue = super.getQueue();
        this.numSchedulesChecked = new AtomicInteger(0);
    }

    @Override
    public List<int[]> generateSchedule() {

        this.closedSchedules.put(originalSchedule.getHashKey(), originalSchedule);
        AlgorithmStep firstStep;
        if ((firstStep = heuristicManager.getAlgorithmStepFromSchedule(originalSchedule)) != null) {
            atomicLong.incrementAndGet();
            execute(firstStep);
        }

        try {
            if (this.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                List<int[]> bestStoredSchedule = getBestStoredSchedule();
                if (bestStoredSchedule != null) {
                    return bestStoredSchedule;
                } else {
                    return listSchedule.rebuildPath();
                }
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

        updateData(algorithmStep);

        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            this.optimalSchedules.add(algorithmStep.rebuildPath());
            shutdown();
        } else {
            for (AlgorithmStep step : this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules))) {
                try {
                    atomicLong.incrementAndGet();
                    execute(step);
                } catch (RejectedExecutionException e) {
                    // This is fine...
                }
            }
            if (atomicLong.decrementAndGet() == 0) {
                System.out.println("Shutting down");
                shutdown();
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
            }
            // Our heuristic is consistent, so we don't have to do anything in an else here.
        }

        return unexploredSchedules;
    }

    public int addReporterTask() {
        return this.numSchedulesChecked.intValue();
    }

    private void updateData(AlgorithmStep step) {
        this.numSchedulesChecked.incrementAndGet();
    }

    private List<int[]> getBestStoredSchedule() {

        List<int[]> bestSchedule = null;
        int bestScheduleLength = Integer.MAX_VALUE;
        int maxLength;
        int endLocation;

        for (List<int[]> schedule : this.optimalSchedules) {

            maxLength = 0;
            for (int[] task : schedule) {

                endLocation = this.originalSchedule.getTaskNode((short) task[0]).getTaskCost() + task[2];
                if (endLocation > maxLength) {
                    maxLength = endLocation;
                }

            }
            if (maxLength < bestScheduleLength) {
                bestScheduleLength = maxLength;
                bestSchedule = schedule;
            }
        }
        return bestSchedule;
    }
}
