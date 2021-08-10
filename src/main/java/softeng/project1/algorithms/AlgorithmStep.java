package softeng.project1.algorithms;

import softeng.project1.graph.Schedule;

import java.util.List;

public class AlgorithmStep implements Comparable<AlgorithmStep>{
    private final int priorityValue;
    private final Schedule schedule;

    AlgorithmStep(int priorityValue, Schedule schedule) {
        this.priorityValue = priorityValue;
        this.schedule = schedule;
    }

    @Override
    public int compareTo(AlgorithmStep o) {
        return this.priorityValue - o.priorityValue;
    }

    public List<Schedule> takeStep() {
        return this.schedule.expand();
    }

    public List<int[]> rebuildPath() {
        return this.schedule.rebuildPath();
    }
}