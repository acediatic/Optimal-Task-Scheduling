package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.algorithms.astar.AlgorithmStep;
import softeng.project1.graph.Schedule;

import java.util.List;

public interface HeuristicManager {

    void addAll(List<Schedule> newFringeSchedules);

    void add(Schedule newFringeSchedule);

    AlgorithmStep get();
}
