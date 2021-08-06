package softeng.project1.graph;

import softeng.project1.graph.tasks.TaskNode;

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
}
