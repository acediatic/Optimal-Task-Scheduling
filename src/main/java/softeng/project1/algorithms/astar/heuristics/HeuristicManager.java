package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;

public interface HeuristicManager {

    void addAllSchedules(List<Schedule> newFringeSchedules);

    void addSchedule(Schedule newFringeSchedule);

    AlgorithmStep getAlgorithmStepQueueHead();
}
