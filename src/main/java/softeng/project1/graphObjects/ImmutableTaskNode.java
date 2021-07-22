package softeng.project1.graphObjects;

import java.util.List;

public class ImmutableTaskNode implements TaskNode{

    private final String taskName;
    private final int taskCost;
    private final List<TaskPrerequisite> parentTaskEdges;
    private final List<TaskPrerequisite> childTaskEdges;

    public ImmutableTaskNode(String taskName, int taskCost, List<TaskPrerequisite> parentTaskEdges, List<TaskPrerequisite> childTaskEdges) {
        this.taskName = taskName;
        this.taskCost = taskCost;
        this.parentTaskEdges = parentTaskEdges;
        this.childTaskEdges = childTaskEdges;
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public int getTaskCost() {
        return this.taskCost;
    }

    @Override
    public List<TaskPrerequisite> getParentTaskEdges() {
        return this.parentTaskEdges;
    }

    @Override
    public List<TaskPrerequisite> getChildTaskEdges() {
        return this.childTaskEdges;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("######################\n");

        stringBuilder
            .append("Immutable Task Node:\n")
            .append("Task Name: ").append(this.taskName).append("\n")
            .append("Task Cost: ").append(this.taskCost).append("\n");
        
        stringBuilder.append("Parent Task Edges:\n");
        for (TaskPrerequisite prerequisite: this.parentTaskEdges) { // Use get method instead?
            stringBuilder
            .append(prerequisite) // Implicit toString() call
            .append("\n");
        }

        stringBuilder.append("Child Task Edges:\n");
        for (TaskPrerequisite childPrerequisite: this.childTaskEdges) {
            stringBuilder
            .append(childPrerequisite) // Implicit toString() call
            .append("\n");
        }

        stringBuilder.append("######################\n");

        return stringBuilder.toString();
    }

}
