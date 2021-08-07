package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.processors.processor.Processor;
import softeng.project1.graph.tasks.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 */
public class ScheduleState implements Schedule{
    private final OriginalSchedule originalSchedule;
    private final ScheduleStateChange change;
    private final Map<Integer, TaskNode> taskNodes;
    private final Map<Integer, TaskNode> freeNodes;
    private final Processors processors;

    private final int maxBottomLevel;
    private final int maxDataReadyTime;

    protected ScheduleState(
            OriginalSchedule originalSchedule,
            ScheduleStateChange change,
            Map<Integer, TaskNode> taskNodes,
            Map<Integer, TaskNode> freeNodes,
            Processors processors,
            int maxBottomLevel,
            int maxDataReadyTime) {
        this.originalSchedule = originalSchedule;
        this.change = change;
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
    public boolean deepEquals(Schedule otherSchedule) {
        try {
            return ((ScheduleState) otherSchedule).processors.deepEquals(this.processors);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int getMaxDataReadyTime() {
        return this.maxDataReadyTime;
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
            // TODO... Prune unnecessary processors additions
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
                expandedSchedules.add(new ScheduleState(
                        this.originalSchedule,
                        new ScheduleStateChange(
                                this.change,
                                freeTask,
                                processorID,
                                insertLocation
                        ),
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

    public int getIdleTime() {
        return this.processors.getIdleTime();
    }

    public int getMaxBottomLevel() {
        return maxBottomLevel;
    }

    private void fillProcessorPrerequisites(int insertPoint, int processorID, int taskLength, int communicationCost, int[] arrayToFill) {

        int taskEndPoint = insertPoint + taskLength;
        int communicatedPrerequisite = taskEndPoint + communicationCost;

        // TODO... Find faster way of doing this
        for (int i = 0; i < arrayToFill.length; i++) {
            if (i == processorID) {
                arrayToFill[i] = taskEndPoint;
            } else {
                arrayToFill[i] = communicatedPrerequisite;
            }
        }

    }

    private TaskNode getTaskNode(int taskID) {
        TaskNode returnNode;
        if ((returnNode = this.taskNodes.get(taskID)) != null) {
            return returnNode;
        } else {
            return this.originalSchedule.getTaskNode(taskID);
        }
    }

}
