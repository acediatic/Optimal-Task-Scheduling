package softeng.project1.io;

import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.graph.Schedule;

import java.util.List;

/**
 * An interface to mandate the methods supported by an IOHandler, be it list scheduler or
 * A* implementation. Contains basic methods supporting I/O.
 */
public interface IOHandler {

    /**
     * Read the file and create the initial, empty schedule
     *
     * @return the initial, empty schedule
     */
    Schedule readFile();

    /**
     * Write the optimal schedule to file
     *
     * @param scheduledTaskData the optimal schedule
     * @return the string representation of the optimal schedule's final details (length etc.)
     */
    String writeFile(List<int[]> scheduledTaskData);

    /**
     * @return The sum of task weights for this input graph. Used by the heuristic
     */
    int getSumWeights();

    /**
     * @return the topologically sorted list of nodes, representing tasks
     */
    AlgorithmStep getListSchedulingAlgoStep();
}
