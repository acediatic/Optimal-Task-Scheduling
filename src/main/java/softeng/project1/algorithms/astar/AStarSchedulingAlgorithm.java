package softeng.project1.algorithms.astar;

import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.graph.Schedule;

import java.util.List;

/**
 * An interface to provide minimum requirements for the scheduling algorithm, and so they
 * can be used interchangeably from the main method.
 */
public interface AStarSchedulingAlgorithm extends SchedulingAlgorithm {

    /**
     * Removes already checked schedules from the given list and then stores them.
     * @param fringeSchedules : List of generated partial schedules.
     * @return : List of pruned partial schedules which should be added to the priority queue
     */
    List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> fringeSchedules);

}
