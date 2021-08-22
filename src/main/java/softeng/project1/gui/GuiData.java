package softeng.project1.gui;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;

import java.util.List;

public class GuiData {

    private final List<int[]> currentBest;
    private final int currentHeuristic;
    private final AlgorithmState state;
    private final int numSchedulesChecked;

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
