package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ScheduleState implements Schedule {

    protected final Map<Integer, TaskNode> taskNodes;
    protected final Map<Integer, TaskNode> freeNodes;
    protected final Processors processors;
    private final int maxBottomLevel;
    private final int maxDataReadyTime;

    protected ScheduleState(Map<Integer, TaskNode> taskNodes,
                            Map<Integer, TaskNode> freeNodes,
                            Processors processors,
                            int maxBottomLevel,
                            int maxDataReadyTime) {
        this.taskNodes = taskNodes;
        this.freeNodes = freeNodes;
        this.processors = processors;
        this.maxBottomLevel = maxBottomLevel;
        this.maxDataReadyTime = maxDataReadyTime;
    }

    @Override
    public long getHashKey() {
        return this.processors.murmurHash();
    }

    @Override
    public int getMaxBottomLevel() {
        return this.maxBottomLevel;
    }

    @Override
    public int getMaxDataReadyTime() {
        return this.maxDataReadyTime;
    }

    @Override
    public int getIdleTime() {
        return this.processors.getIdleTime();
    }

    @Override
    public List<Schedule> expand() {

        // TODO... Handle success state when no free nodes

        // Initialising empty array here for speed reasons
        int[] processorPrerequisites = new int[processors.getNumProcessors()];
        List<Schedule> expandedSchedules = new ArrayList<>();

        // Iterating through all free tasks
        for (TaskNode freeTask: this.freeNodes.values()) {

            // Iterating through all processors
            // TODO... Prune unnecessary processor additions
            for (int processorID = 0; processorID < processors.getNumProcessors(); processorID++) {

                // Inserting task into processor and getting new objects
                Processors newProcessors = this.processors.copyAndAddProcessor(freeTask, processorID);
                int insertLocation = newProcessors.getProcessor(processorID).getLastInsertLocation();

                Map<Integer, TaskNode> newFreeNodes = new HashMap<>(this.freeNodes); // Shallow copy
                Map<Integer, TaskNode> newTaskNodes = new HashMap<>(this.taskNodes); // Shallow copy
                // Inserted task no longer needs to be stored
                newFreeNodes.remove(freeTask.getTaskID());
                newTaskNodes.remove(freeTask.getTaskID());

                // Iterating through all changed children
                int[][] changedChildLinks = freeTask.getChildLinks();

                for (int[] childLink: changedChildLinks) {
                    // Get old version of changed child from own storage or from original if not in own
                    TaskNode changedChild = this.getTaskNode(childLink[0]);
                    // Generate array of prerequisite locations
                    fillProcessorPrerequisites(
                            insertLocation,
                            changedChild.getTaskCost(),
                            processorID,
                            childLink[1],
                            processorPrerequisites
                    );
                    // Make new version of changed child
                    changedChild = changedChild.copyAndSetPrerequisite(processorPrerequisites);

                    // Put new changed child in storage
                    newTaskNodes.put(changedChild.getTaskID(), changedChild); // Should overwrite old one
                    if (changedChild.isFree()) {
                        newFreeNodes.put(changedChild.getTaskID(), changedChild);
                    }
                }

                // Make new Schedule object to hold changed data
                expandedSchedules.add(new ChangedScheduleState(
                        getOriginalSchedule(),
                        generateStateChange(freeTask, processorID, insertLocation),
                        newTaskNodes,
                        newFreeNodes,
                        newProcessors,
                        Math.max(this.maxBottomLevel, freeTask.getBottomLevel()),
                        Math.max(this.maxDataReadyTime, insertLocation + freeTask.getTaskCost() + freeTask.getMaxCommunicationCost())
                ));
            }
        }
        return expandedSchedules;
    }

    private static void fillProcessorPrerequisites(int insertPoint, int processorID, int taskLength, int communicationCost, int[] arrayToFill) {

        int taskEndPoint = insertPoint + taskLength;
        int communicatedPrerequisite = taskEndPoint + communicationCost;

        // TODO... Find faster way of doing this
        for (int i = -1; i < arrayToFill.length; i++) {
            if (i == processorID) {
                arrayToFill[i] = taskEndPoint;
            } else {
                arrayToFill[i] = communicatedPrerequisite;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-----------------------------\n");
        stringBuilder.append("Schedule State:\n");

        stringBuilder.append("Free Task Nodes: \n");
        for (TaskNode freeTaskNode: this.freeNodes.values()) {
            stringBuilder.append(freeTaskNode).append("\n"); // Implicit toString() method call
        }

        stringBuilder.append("General Task Nodes: \n");
        for (TaskNode taskNode: this.taskNodes.values()) {
            if (!this.freeNodes.containsKey(taskNode.getTaskID())) {
                stringBuilder.append(taskNode).append("\n"); // Implicit toString() method call
            }
        }

        stringBuilder.append("-----------------------------\n");
        return stringBuilder.toString();
    }

    protected abstract OriginalScheduleState getOriginalSchedule();

    protected abstract ScheduleStateChange generateStateChange(TaskNode freeTask, int processorID, int insertLocation);

    protected abstract TaskNode getTaskNode(int taskID);





}
