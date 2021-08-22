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

/**
 * This class is responsible for managing the parallel execution of the search for the optimal schedule.
 * It uses a thread pool executor and a blocking queue to simultaneously explore multiple schedules, returning
 * when the optimal is found.
 */
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

        this.heuristicManager = heuristicManager;
        this.closedSchedules = new ConcurrentHashMap<>();
        this.optimalSchedules = new ArrayList<>();
        this.originalSchedule = originalSchedule;
        this.listSchedule = listSchedule;
        this.atomicLong = new AtomicLong();
        this.state = AlgorithmState.WAITING;
    }

    /**
     * Generate schedule is the main method for the scheduling algorithm.
     * It is used to turn the provided original schedule into the optimal schedule.
     * This is done by repeatedly expanding schedules, and placing them in a priority
     * queue till the optimal is found, at which case this method returns
     *
     * @return List<int [ ]> generatedSchedule, the optiaml scheduling for the given original schedule state.
     */
    @Override
    public List<int[]> generateSchedule() {

        this.state = AlgorithmState.ACTIVE;
        this.closedSchedules.put(originalSchedule.getHashKey(), originalSchedule);
        AlgorithmStep firstStep;
        if ((firstStep = heuristicManager.getAlgorithmStepFromSchedule(originalSchedule)) != null) {
            // Adding a step to the queue, so increment the atomic count.
            atomicLong.incrementAndGet();
            // submit to pool for execution.
            execute(firstStep);
        }

        try {
            if (this.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                List<int[]> bestStoredSchedule = getBestStoredSchedule();
                // Algorithm state used by the GUI, notify that we've found the optimal solution.
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

    /**
     * A helper method to add an optimal schedule to the list. Useful
     * when parallel threads simultaneously find optimal solutions.
     * In this case, the best heuristic value is kept, with ties broken
     * randomly.
     */
    public synchronized void addOptimal(AlgorithmStep algorithmStep) {
        this.optimalSchedules.add(algorithmStep);
    }


    /**
     * Hook method from ThreadPoolExecutor that runs after each Runnable completes its execution.
     * Used here to retrieve the generated schedules from the AlgorithmStep's run() method and then handle them.
     * @param runnable : The AlgorithmStep object which just finished running its schedule's expand() method.
     */
    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        List<Schedule> fringeSchedules;
        AlgorithmStep algorithmStep = (AlgorithmStep) runnable;

        // null indicates that it was unable to create a new schedule, indicating
        // all tasks have been scheduled and that we've found the optimal.
        // so we add and return this value.
        if ((fringeSchedules = algorithmStep.getFringeSchedules()) == null) {
            addOptimal(algorithmStep);
            clearAndShutdown();
        } else {
            List<AlgorithmStep> algoSteps = this.heuristicManager.getAlgorithmStepsFromSchedules(pruneExpandedSchedulesAndAddToMap(fringeSchedules));
            int algoStepsSize;
            if ((algoStepsSize = algoSteps.size()) != 0) {
                // increment the count by the number of schedules we're adding to explore
                atomicLong.addAndGet(algoStepsSize);
                for (AlgorithmStep algoStep : algoSteps) {
                    try {
                        execute(algoStep);
                    } catch (RejectedExecutionException e) {
                        // pool is shutting down
                    }
                }
            }
            // decrement the count as this current state has now been explored.
            // if the count is less than 0, we know the list schedule is optimal.
            if (atomicLong.decrementAndGet() <= 0) {
                clearAndShutdown();
            }
        }
    }

    /**
     * This method clears the thread, and shuts down. This allows the current
     * threads to finish execution, in order to avoid a race condition if the
     * optimal schedule and another non-optimal schedule are found at the \
     * same time.
     */
    private synchronized void clearAndShutdown() {
        shutdown();
        if (this.getQueue().peek() != null) {
            this.getQueue().clear();
        }
    }


    /**
     * This prunes the expanded schedules we've already explored, and adds those that
     * haven't been seen before to the map of now seen schedules. This uses a hash,
     * where collisions are resolved by using a deep equals check. The consistency
     * of our heuristic allows us to do nothing if we have already explored the schedule.
     *
     * @param expandedSchedules the schedules to prune and add
     * @return the list of as yet unexplored schedules that should now be explored
     */
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

    /**
     * A helper method to transfer data from the running algorithm to the GUI.
     *
     * @return GuiData, a data object storing information needed by the GUI.
     */
    @Override
    public GuiData getGuiData() {

        if (this.state == AlgorithmState.WAITING) {
            return null;
            // we only show data if the algorithm is running
        } else if (this.state == AlgorithmState.ACTIVE) {
            return new GuiData(
                    (AlgorithmStep) this.getQueue().peek(),
                    this.state,
                    this.closedSchedules.size()
            );
        } else {
            // or finished.
            return new GuiData(
                    this.bestScheduleStep,
                    this.state,
                    this.closedSchedules.size()
            );
        }
    }

    /**
     * A method to fetch the optimal schedule found by this algorithm.
     * Used by the GUI to display the final result
     *
     * @return the optimal schedule as a list of int[]
     */
    private List<int[]> getBestStoredSchedule() {
        Collections.sort(optimalSchedules);
        if (this.optimalSchedules.size() == 0) {
            this.optimalSchedules.add(listSchedule);
        }
        this.bestScheduleStep = this.optimalSchedules.get(0);
        return bestScheduleStep.rebuildPath();
    }
}
