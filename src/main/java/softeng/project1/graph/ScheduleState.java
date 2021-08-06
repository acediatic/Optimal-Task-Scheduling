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

    private int maxBottomLevel = 0;
    private int idleTime = 0;

    protected ScheduleState(
            OriginalSchedule originalSchedule,
            ScheduleStateChange change,
            Map<Integer, TaskNode> taskNodes,
            Map<Integer, TaskNode> freeNodes,
            Processors processors) {
        this.originalSchedule = originalSchedule;
        this.change = change;
        this.taskNodes = taskNodes;
        this.freeNodes = freeNodes;
        this.processors = processors;
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
        return 0; // TODO...
    }

    // TODO
    // calculate bottom level, idle time increment for each added task here too.
    // Data Transfer time from ProcessorPrerequisite of each TaskNodeState.
    @Override
    public List<Schedule> expand() {
        int[] processorPrerequisites = new int[processors.getNumProcessors()];

        List<Schedule> expandedSchedules = new ArrayList<>();

        for (TaskNode freeTask: this.freeNodes.values()) {

            for (int processorID = 0; processorID < processors.getNumProcessors(); processorID++) {

                Processor newProcessor = processors.getProcessor(processorID).copyAndInsert(freeTask);
                Processors newProcessors = this.processors.copyAndAddProcessor(newProcessor);

                int[][] changedChildLinks = freeTask.getChildLinks();
                Map<Integer, TaskNode> newFreeNodes = new HashMap<>(this.freeNodes); // Shallow copy
                Map<Integer, TaskNode> newTaskNodes = new HashMap<>(this.taskNodes); // Shallow copy

                for (int[] childLink: changedChildLinks) {
                    TaskNode changedChild = this.getTaskNode(childLink[0]);
                    fillProcessorPrerequisites(
                            newProcessor.getLastInsert(),
                            changedChild.getTaskCost(),
                            processorID,
                            childLink[1],
                            processorPrerequisites
                    );
                    changedChild = changedChild.copyAndSetPrerequisite(processorPrerequisites);

                    newTaskNodes.put(changedChild.getTaskID(), changedChild); // Should overwrite
                    if (changedChild.isFree()) {
                        newFreeNodes.put(changedChild.getTaskID(), changedChild);
                    }
                }

                expandedSchedules.add(new ScheduleState(
                        this.originalSchedule,
                        new ScheduleStateChange(
                                this.change,
                                freeTask,
                                newProcessor.getID(),
                                newProcessor.getLastInsert()
                        ),
                        newTaskNodes,
                        newFreeNodes,
                        newProcessors
                ));
            }
            return expandedSchedules;
        }

        return null;
    }

    public int getIdleTime() {
        return idleTime;
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
