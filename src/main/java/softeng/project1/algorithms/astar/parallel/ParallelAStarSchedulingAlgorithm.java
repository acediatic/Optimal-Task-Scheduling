package softeng.project1.algorithms.astar.parallel;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;
import softeng.project1.gui.GuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelAStarSchedulingAlgorithm extends ThreadPoolExecutor implements AStarSchedulingAlgorithm {

    private static final long KEEP_ALIVE_TIME_MILLISECONDS = 0;

    private final HeuristicManager heuristicManager;
    private final Map<Processors, Schedule> closedSchedules;
    private final ArrayList<AlgorithmStep> optimalSchedules;
    private final Schedule originalSchedule;
    private final AlgorithmStep listSchedule;
    private final AtomicLong atomicLong;
    private AlgorithmState state;
    private AlgorithmStep bestScheduleStep;

    public ParallelAStarSchedulingAlgorithm(Schedule originalSchedule,
                                            HeuristicManager heuristicManager,
                                            int numThreads,
                                            AlgorithmStep listSchedule) {
        super(numThreads, numThreads, KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

        // TODO... give map a useful original size
        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new ArrayList<>();
        this.originalSchedule = originalSchedule;
        this.listSchedule = listSchedule;
        this.atomicLong = new AtomicLong();
        this.state = AlgorithmState.WAITING;
    }

    @Override
    public List<int[]> generateSchedule() {

        this.state = AlgorithmState.ACTIVE;
        this.closedSchedules.put(originalSchedule.getHashKey(), originalSchedule);
        AlgorithmStep firstStep;
        if ((firstStep = heuristicManager.getAlgorithmStepFromSchedule(originalSchedule)) != null) {
            atomicLong.incrementAndGet();
            execute(firstStep);
        }

        try {
            if (this.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                List<int[]> bestStoredSchedule = getBestStoredSchedule();
                this.state = AlgorithmState.FINISHED;
                return bestStoredSchedule;
            } else {
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public synchronized void addOptimal(AlgorithmStep algorithmStep) {
        this.optimalSchedules.add(algorithmStep);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        List<Schedule> fringeSchedules;
        AlgorithmStep algorithmStep = (AlgorithmStep) runnable;

        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            addOptimal(algorithmStep);
            clearAndShutdown();
        } else {
            List<AlgorithmStep> algoSteps = this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules));
            int algoStepsSize;
            if ((algoStepsSize = algoSteps.size()) != 0) {
                atomicLong.addAndGet(algoStepsSize);
                for (AlgorithmStep algoStep : algoSteps) {
                    try {
                        execute(algoStep);
                    } catch (RejectedExecutionException e) {
                        // pool is shutting down
                    }
                }
            }
            if (atomicLong.decrementAndGet() <= 0) {
                clearAndShutdown();
            }
        }
    }

    private synchronized void clearAndShutdown() {
        shutdown();
        if (this.getQueue().peek() != null) {
            this.getQueue().clear();
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

    @Override
    public GuiData getGuiData() {

        if (this.state == AlgorithmState.WAITING) {
            return null;
        } else if (this.state == AlgorithmState.ACTIVE) {
            return new GuiData(
                    (AlgorithmStep) this.getQueue().peek(),
                    this.state,
                    this.closedSchedules.size()
            );
        } else {
            return new GuiData(
                    this.bestScheduleStep,
                    this.state,
                    this.closedSchedules.size()
            );
        }
    }

    private List<int[]> getBestStoredSchedule() {
        Collections.sort(optimalSchedules);
        if (this.optimalSchedules.size() == 0) {
            this.optimalSchedules.add(listSchedule);
        }
        this.bestScheduleStep = this.optimalSchedules.get(0);
        return bestScheduleStep.rebuildPath();
    }
}
