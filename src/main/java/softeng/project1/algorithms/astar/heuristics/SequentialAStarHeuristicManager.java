package softeng.project1.algorithms.astar.heuristics;

import softeng.project1.graph.Schedule;

public class SequentialAStarHeuristicManager extends AStarHeuristicManager {
    public SequentialAStarHeuristicManager(int taskLengthsSum, short numberOfProcesses, AlgorithmStep listScheduling) {
        super(taskLengthsSum, numberOfProcesses, listScheduling);
    }

    @Override
    public AlgorithmStep getAlgoStep(Schedule fringeSchedule) {
        AlgorithmStep algorithmStep = new AlgorithmStep(calculateHeuristicValue(fringeSchedule), fringeSchedule);
        if (algorithmStep.getPriorityValue() >= listSchedulingPriority) {
            // Don't add to schedule, already have better/equivalent in there.
            return null;
        } else {
            return algorithmStep;
        }
    }

}
