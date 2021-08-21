package softeng.project1.io;

import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.graph.Schedule;

import java.util.List;

public interface IOHandler {

    Schedule readFile();

    String writeFile(List<int[]> scheduledTaskData);

    int getSumWeights();

    AlgorithmStep getListSchedulingAlgoStep();
}
