package softeng.project1.algorithms.astar.sequential;

import softeng.project1.algorithms.astar.AStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;

import java.util.HashMap;

public class SequentialAStarSchedulingAlgorithm extends AStarSchedulingAlgorithm {

    public SequentialAStarSchedulingAlgorithm(Schedule originalSchedule, HeuristicManager heuristicManager) {
        // TODO... give map a useful initial size
        super(originalSchedule, heuristicManager, new HashMap<>());
    }

}
