package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ListSchedulingAlgorithmStep extends AlgorithmStep {
    private List<int[]> path;

    public ListSchedulingAlgorithmStep(int priorityValue, List<int[]> path) {
        super(priorityValue, null);
        this.path = path;
    }

    public List<Schedule> takeStep() {
        // we're already done.
        return null;
    }

    @Override
    public void run() {
    }

    // TODO Remus, is this what I should return? or null?
    public List<Schedule> getFringeSchedules() {
        return new ArrayList<Schedule>();
    }

    public List<int[]> rebuildPath() {
        return this.path;
    }
}