package softeng.project1.graph.tasks;

import softeng.project1.graph.tasks.edges.ListCommunicationCost;

import java.util.Arrays;

/**
 * TODO...
 */
public class ListTask {

    private final int taskID;
    private final int weight;
    private final int[] processorPrerequisites;

    private int start;
    private int processor;

    public ListTask(int taskID, int weight, int numberOfProcessors) {
        this.weight = weight;
        this.taskID = taskID;
        this.processorPrerequisites = new int[numberOfProcessors];
    }

    public void setLocation (int start, int processorID) {
        this.start = start;
        this.processor = processorID;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getPrerequisite(int processorID) {
        return this.processorPrerequisites[processorID];
    }

    public int[] getAsIntArray() {
        return new int[]{this.taskID, this.processor, this.start};
    }


    public void notifyChildren(int processorID, int insertPoint, ListCommunicationCost[] children) {

        this.setLocation(insertPoint, processorID);

        int[] fulfilledPrerequisite = new int[this.processorPrerequisites.length];
        int taskEndPoint = insertPoint + this.weight;

        for (ListCommunicationCost communicationCost: children) {

            Arrays.fill(fulfilledPrerequisite, taskEndPoint + communicationCost.getCost());
            fulfilledPrerequisite[processorID] = taskEndPoint;

            communicationCost.getChildTask().notifyPrerequisiteFulfilled(fulfilledPrerequisite);
        }
    }

    public void notifyPrerequisiteFulfilled(int[] fulfilledPrerequisite) {
        for (int i = 0; i < this.processorPrerequisites.length; i++) {
            this.processorPrerequisites[i] = Math.max(this.processorPrerequisites[i], fulfilledPrerequisite[i]);
        }

    }
}
