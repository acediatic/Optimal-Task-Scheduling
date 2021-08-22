package softeng.project1.algorithms;

/**
 * A utility class to convey the three possible states of the algorithm
 * to the GUI, so it knows what to display.
 */
public enum AlgorithmState {
    WAITING,    // the algorithm is not yet running
    ACTIVE,     // the algorithm is searching for the optimal solution
    FINISHED    // the algorithm has found the optimal solution
}
