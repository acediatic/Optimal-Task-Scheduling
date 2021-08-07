package softeng.project1.algorithms.heuristics;

import softeng.project1.algorithms.AlgorithmStep;
import softeng.project1.graph.Schedule;

public interface HeuristicManager {

    void add(Schedule fringeSchedule);

    AlgorithmStep get();
}
