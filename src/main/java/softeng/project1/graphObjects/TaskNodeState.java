package softeng.project1.graphObjects;

import java.util.List;

public class TaskNodeState implements TaskNode {

    private final TaskNode originalTaskNode;

    private final List <TaskPrerequisite> changedParentTaskEdges;
    private final List <TaskPrerequisite> changedChildTaskEdges;

    public TaskNodeState(TaskNode previousState, TaskPrerequisite changedParentTaskEdge) {

        this.originalTaskNode = previousState.getOriginalTaskNode();

        this.changedParentTaskEdges = previousState.getParentTaskEdges();
        // Removing the old copy of the changedParentTaskEdge
        this.changedParentTaskEdges.removeIf(prerequisite ->
                prerequisite.getParentTask().getTaskName()
                                .equals(changedParentTaskEdge.getParentTask().getTaskName()
                                )
        );
        this.changedParentTaskEdges.add(changedParentTaskEdge);

        this.changedChildTaskEdges = previousState.getChildTaskEdges();
    }

    @Override
    public String getTaskName() {
        return originalTaskNode.getTaskName();
    }

    @Override
    public int getTaskCost() {
        return originalTaskNode.getTaskCost();
    }

    @Override
    public List<TaskPrerequisite> getParentTaskEdges() {
        return this.changedParentTaskEdges;
    }

    @Override
    public List<TaskPrerequisite> getChildTaskEdges() {
        return this.changedChildTaskEdges;
    }

    @Override
    public TaskNode getOriginalTaskNode() {
        return this.originalTaskNode;
    }
}
