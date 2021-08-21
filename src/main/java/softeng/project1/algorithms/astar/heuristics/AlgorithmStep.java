package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;

public class AlgorithmStep implements Comparable<AlgorithmStep>, Runnable{
    private final int priorityValue;
    private final Schedule schedule;
    private List<Schedule> generatedSchedules;

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

    @Override
    public void run() {
        this.generatedSchedules = this.schedule.expand();
    }

    public int getPriorityValue() {
        return this.priorityValue;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public List<Schedule> getFringeSchedules() {
        return this.generatedSchedules;
    }

    public List<int[]> rebuildPath() {
        return this.schedule.rebuildPath();
    }
}