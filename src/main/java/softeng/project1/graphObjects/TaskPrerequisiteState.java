package softeng.project1.graphObjects;

public class TaskPrerequisiteState implements TaskPrerequisite {

    private final int communicationCost;
    private final TaskNode parentTask;
    private final TaskNode childTask;

    private final boolean isFulfilled;

    public TaskPrerequisiteState(TaskPrerequisite previousState, TaskNode newTaskNode) {

        this.communicationCost = previousState.getCommunicationCost();

        // This could be optimised
        if (previousState.getParentTask().getTaskName().equals(newTaskNode.getTaskName())) {
            this.parentTask = newTaskNode;
            this.childTask = previousState.getChildTask();
            this.isFulfilled = true;
        } else if (previousState.getChildTask().getTaskName().equals(newTaskNode.getTaskName())) {
            this.parentTask = previousState.getParentTask();
            this.childTask = newTaskNode;
            this.isFulfilled = false;
        } else {
            throw new RuntimeException(""); // TODO...
        }
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

    public boolean isFulfilled() {
        return this.isFulfilled;
    }

}
