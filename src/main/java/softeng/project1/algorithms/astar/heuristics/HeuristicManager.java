package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

import java.util.List;

public interface HeuristicManager {

    List<AlgorithmStep> getAlgorithmStepsFromSchedules(List<Schedule> newFringeSchedules);

    AlgorithmStep getAlgorithmStepFromSchedule(Schedule newFringeSchedule);

}
