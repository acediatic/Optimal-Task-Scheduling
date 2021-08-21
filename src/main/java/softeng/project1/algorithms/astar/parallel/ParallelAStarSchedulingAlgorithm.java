package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;
import softeng.project1.gui.GuiData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelAStarSchedulingAlgorithm extends ThreadPoolExecutor implements AStarSchedulingAlgorithm {

    private static final long KEEP_ALIVE_TIME_MILLISECONDS = 0;

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;
    private final List<List<int[]>> optimalSchedules;
    private final Schedule originalSchedule;

    private AlgorithmState state;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads) {
        super(numThreads, numThreads, KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

        // TODO... give map a useful original size
        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new CopyOnWriteArrayList<>();
        this.originalSchedule = originalSchedule;
        this.state = AlgorithmState.WAITING;
    }

    @Override
    public List<int[]> generateSchedule() {

        this.state = AlgorithmState.ACTIVE;
        this.closedSchedules.put(originalSchedule.getHashKey(), originalSchedule);
        AlgorithmStep firstStep;
        if ((firstStep = heuristicManager.getAlgorithmStepFromSchedule(originalSchedule)) != null) {
            execute(firstStep);
        } else {
            // TODO... Return list schedule
        }

        try {
            if (this.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                this.state = AlgorithmState.FINISHED;
                return getBestStoredSchedule();
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

            for (AlgorithmStep step: this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules))) {
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

    public GuiData getGuiData() {
        return new GuiData((AlgorithmStep) this.getQueue().peek(), this.state, this.closedSchedules.size());
    }

    private List<int[]> getBestStoredSchedule() {

        List<int[]> bestSchedule = null;
        int bestScheduleLength = Integer.MAX_VALUE;
        int maxLength;
        int endLocation;

        for (List<int[]> schedule: this.optimalSchedules) {

            maxLength = 0;
            for (int[] task: schedule) {

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
