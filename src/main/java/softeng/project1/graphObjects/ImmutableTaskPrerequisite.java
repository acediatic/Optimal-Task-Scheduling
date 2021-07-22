package softeng.project1.graphObjects;

public class ImmutableTaskPrerequisite implements TaskPrerequisite {



    private final int communicationCost;
    private final TaskNode parentTask;
    private final TaskNode childTask;

    public ImmutableTaskPrerequisite(int communicationCost, TaskNode parentTask, TaskNode childTask) {

        this.communicationCost = communicationCost;
        this.parentTask = parentTask;
        this.childTask = childTask;

    }

    @Override
    public int getCommunicationCost() {
        return this.communicationCost;
    }
    

    @Override
    public TaskNode getParentTask() {
        return this.parentTask;
    }

    @Override
    public TaskNode getChildTask() {
        return this.childTask;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder
            .append("Immutable Task Prerequisite:\n")
            .append("Parent task: ").append(this.parentTask.getTaskName()).append("\n")
            .append("Child task: ").append(this.childTask.getTaskName()).append("\n")
            .append("Communication Cost: ").append(this.communicationCost);

        return stringBuilder.toString();
    }

}
