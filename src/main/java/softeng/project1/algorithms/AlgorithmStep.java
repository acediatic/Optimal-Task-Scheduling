package softeng.project1.algorithms;

import softeng.project1.graph.Schedule;

public class AlgorithmStep {
    private int priorityValue;
    private Schedule schedule;

    AlgorithmStep(int priorityValue, Schedule schedule) {
        this.priorityValue = priorityValue;
        this.schedule = schedule;
    }
}