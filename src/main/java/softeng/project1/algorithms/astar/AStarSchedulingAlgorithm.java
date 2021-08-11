package softeng.project1.algorithms.astar;

import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface AStarSchedulingAlgorithm extends SchedulingAlgorithm {

    public List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> fringeSchedules);

}
