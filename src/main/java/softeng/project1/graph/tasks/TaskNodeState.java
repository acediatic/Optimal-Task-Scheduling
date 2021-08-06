package softeng.project1.graph.tasks;

import softeng.project1.graph.tasks.TaskNode;

/**
 * TODO...
 */
public class TaskNodeState implements TaskNode {

    private final TaskNode originalTaskNode;
    private final int numLinks;
    private final int[] processorPrerequisites;

    protected TaskNodeState(TaskNode originalTaskNode, int numLinks, int[] processorPrerequisites) {
        this.originalTaskNode = originalTaskNode;
        this.numLinks = numLinks;
        this.processorPrerequisites = processorPrerequisites;
    }

    @Override
    public int getTaskID() {
        return originalTaskNode.getTaskID();
    }

    @Override
    public int getTaskCost() {
        return originalTaskNode.getTaskCost();
    }

    @Override
    public TaskNode getOriginalTaskNode() {
        return this.originalTaskNode;
    }

    @Override
    public boolean isFree() {
        return this.numLinks == 0;
    }

    @Override
    public int getProcessorPrerequisite(int processorID) {
        return this.processorPrerequisites[processorID];
    }

    @Override
    public int[] getAllPrerequisites() {
        return this.processorPrerequisites;
    }

    @Override
    public TaskNode copyAndSetPrerequisite(int[] parentPrerequisites) {
        int[] newPrerequisites = new int[this.processorPrerequisites.length];

        for (int i = 0; i < this.processorPrerequisites.length; i++) {
            newPrerequisites[i] = Math.max(
                    this.processorPrerequisites[i],
                    parentPrerequisites[i]
            );
        }

        return new TaskNodeState(
                this.originalTaskNode,
                this.numLinks - 1,
                newPrerequisites
        );
    }
}
