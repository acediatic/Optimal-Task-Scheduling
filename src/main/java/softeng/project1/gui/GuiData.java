package softeng.project1.gui;

import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.graph.Schedule;

public class GuiData {

    private final Schedule currentBest;
    private final int currentHeuristic;
    private final AlgorithmState state;
    private final int numSchedulesChecked;

    public GuiData(AlgorithmStep step, AlgorithmState state, int numSchedulesChecked) {

        if (step == null) {
            this.currentBest = null;
            this.currentHeuristic = 0;
        } else {
            this.currentBest = step.getSchedule();
            this.currentHeuristic = step.getPriorityValue();
        }

        this.state = state;
        this.numSchedulesChecked = numSchedulesChecked;

    }


    public Schedule getCurrentBestSchedule() {
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
