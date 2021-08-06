package softeng.project1.graph;

import softeng.project1.graph.tasks.TaskNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleStateChange {
    private ScheduleStateChange previousChange;
    private TaskNode insertedTaskNode;
    private int processorID;
    private int insertionTime;

    public ScheduleStateChange(ScheduleStateChange previousChange, TaskNode insertedTaskNode, int processorID, int insertionTime) {
        this.previousChange = previousChange;
        this.insertedTaskNode = insertedTaskNode;
        this.processorID = processorID;
        this.insertionTime = insertionTime;
    }

    public List<int[]> rebuildSolutionPath() {
        int[] pathData = new int[]{processorID, insertionTime, insertedTaskNode.getTaskID()};

        // First step
        if (previousChange == null) {
            return Arrays.asList(new int[][]{pathData});
        } else {
            List<int[]> returnList = previousChange.rebuildSolutionPath();
            returnList.add(pathData);
            return returnList;
        }
    }
}
