package softeng.project1.algorithms;

import softeng.project1.graph.Schedule;

public class AlgorithmStep implements Comparable<AlgorithmStep>{
    private int priorityValue;
    private Schedule schedule;

    AlgorithmStep(int priorityValue, Schedule schedule) {
        this.priorityValue = priorityValue;
        this.schedule = schedule;
    }

    @Override
    public int compareTo(AlgorithmStep o) {
        return this.priorityValue - o.priorityValue;
    }
}