package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;

/**
 * The AlgorithmStep class provides information to turn a partial schedule
 * into a runnable that can be explored, especially the thread pool of the
 * parallel implementation. It implements comparable, so that it can be
 * sorted into the priority queue, and implements runnable, so that it can
 * be run by a thread.
 */
public class AlgorithmStep implements Comparable<AlgorithmStep>, Runnable {
    private final int priorityValue;
    private final Schedule schedule;
    private List<Schedule> generatedSchedules;

    AlgorithmStep(int priorityValue, Schedule schedule) {
        this.priorityValue = priorityValue;
        this.schedule = schedule;
    }

    /**
     * The CompareTo method allows for these objects to be sorted in the
     * priority queue by their heuristic priority value.
     *
     * @param o another Alogrithm Step.
     * @return int: <0 implies this < other
     * =0 implies this == other
     * >0 implies this > other
     */
    @Override
    public int compareTo(AlgorithmStep o) {
        return this.priorityValue - o.priorityValue;
    }

    /**
     * @return the heuristic priority value for this algorithm step.
     */
    public int getPriorityValue() {
        return priorityValue;
    }

    /**
     * Explores the schedules for this algorithm step, by assigning
     * free tasks to processors.
     *
     * @return the list of partial schedules created by expanding this one
     */
    public List<Schedule> takeStep() {
        return this.schedule.expand();
    }

    /**
     * Parallel equivalent to {@link #takeStep() takeStep} method.
     */
    @Override
    public void run() {
        this.generatedSchedules = this.schedule.expand();
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    /**
     * @return the schedules created by the expansion of this algorithm step's
     * schedule
     */
    public List<Schedule> getFringeSchedules() {
        return this.generatedSchedules;
    }

    /**
     * Rebuild path turns the Schedule object (which is esentially a series
     * of diffs) to an actual int array which represents the scheduling.
     * It does this by making its way up the path, rebuilding into a schedule.
     * Hence, rebuildPath
     *
     * @return the schedule, represented as a List of int arrays.
     */
    public List<int[]> rebuildPath() {
        return this.schedule.rebuildPath();
    }
}