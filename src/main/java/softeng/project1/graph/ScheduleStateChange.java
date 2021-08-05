package softeng.project1.graph;

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

        int previousBottomLevel = previousChange.getBottomLevel();




    }

    public int getBottomLevel() {

    }
}
