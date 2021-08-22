package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * The list scheduling class is a special implementation of the algorithm step, as it represents an already
 * completed schedule, with no scheduling object path. This means it provides some functionality, whilst
 * having empty-body implementations of others.
 */
public class ListSchedulingAlgorithmStep extends AlgorithmStep {
    private final List<int[]> path;

    public ListSchedulingAlgorithmStep(int priorityValue, List<int[]> path) {
        super(priorityValue, null);
        this.path = path;
    }

    /**
     * Take step does nothing, as the schedule is already created
     *
     * @return null;
     */
    public List<Schedule> takeStep() {
        // we're already done.
        return null;
    }

    /**
     * Run does nothing, as the schedule is already created.
     */
    @Override
    public void run() {
    }

    /**
     * Get fringe schedule returns an empty array list, as this is a complete schedule
     * and has no further free tasks to place.
     *
     * @return empty array list.
     */
    public List<Schedule> getFringeSchedules() {
        return new ArrayList<>();
    }

    /**
     * @return the schedule represented as a list of int arrays that was found
     * by the list scheduler.
     */
    public List<int[]> rebuildPath() {
        return this.path;
    }
}