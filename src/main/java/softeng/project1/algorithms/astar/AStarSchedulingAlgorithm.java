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
     * A method to
     * @param fringeSchedules
     * @return
     */
    List<Schedule> pruneExpandedSchedulesAndAddToMap(List<Schedule> fringeSchedules);

}
