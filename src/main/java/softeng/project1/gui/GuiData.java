package softeng.project1.gui;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;

import java.util.List;

/**
 * A class to store data used by the GUI to display the visualisation
 */
public class GuiData {

    private final List<int[]> currentBest;  // the current best schedule (partial or optimal)
    private final int currentHeuristic;     // the heuristc value of the corresponding schedule
    private final AlgorithmState state;     // the AlgorithmState for the corresponding schedule
    private final int numSchedulesChecked;  // the approximate number of states checked so far.

    public GuiData(AlgorithmStep step, AlgorithmState state, int numSchedulesChecked) {

        if (step == null) {
            this.currentBest = null;
            this.currentHeuristic = 0;
        } else {
            this.currentBest = step.rebuildPath();
            this.currentHeuristic = step.getPriorityValue();
        }

        this.state = state;
        this.numSchedulesChecked = numSchedulesChecked;

    }


    public List<int[]> getCurrentBestScheduleArray() {
        return this.currentBest;
    }

    public int getHeuristic() {
        return this.currentHeuristic;
    }

    public AlgorithmState getAlgorithmState() {
        return this.state;
    }

    public int getNumSchedulesChecked() {
        return this.numSchedulesChecked;
    }


}
