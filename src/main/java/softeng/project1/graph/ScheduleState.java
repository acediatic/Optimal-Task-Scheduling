package softeng.project1.graph;

import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.TaskNode;

import java.util.*;

/**
 * @author Remus Courtenay
 * @version 1.0.1
 * @since 1.8
 *
 * Abstract implementation of Schedule that represents all possible states of partial schedules.
 * Implements methods common to all ScheduleStates such as getter methods for final fields and the majority of the
 * expand method.
 *
 * ScheduleState represents a partial schedule and as such should be considered immutable.
 * Care should be taken to never directly expose the stored Maps.
 */
public abstract class ScheduleState implements Schedule {

    protected final Map<Short, TaskNode> taskNodes;
    protected final Map<Short, TaskNode> freeNodes;
    protected final Processors processors;
    protected final ScheduleStateChange change;
    protected final int maxBottomLevel;
    protected final int maxDataReadyTime;

    protected ScheduleState(Map<Short, TaskNode> taskNodes,
                            Map<Short, TaskNode> freeNodes,
                            Processors processors,
                            ScheduleStateChange change,
                            int maxBottomLevel,
                            int maxDataReadyTime) {
        this.taskNodes = taskNodes;
        this.freeNodes = freeNodes;
        this.processors = processors;
        this.change = change;
        this.maxBottomLevel = maxBottomLevel;
        this.maxDataReadyTime = maxDataReadyTime;
    }

    /**
     * It's a pain that this directly exposes Processors. Using a HashTable over a HashMap would be preferable so this
     * wasn't required.
     * @return : The Processors object storing data about the current state of the processors for use as a key in the
     *           hashmap required by A*.
     */
    @Override
    public Processors getHashKey() {
        return this.processors;
    }

    /**
     * Generates a set of new fringe Schedules by greedily scheduling each free task onto every processor.
     * The number of created schedules is bounded by O(nm) where n is the number of tasks and m is the number of
     * processors.
     * Pruning techniques are utilised to reduce this number by attempting to only generating unique schedules.
     *
     * Once expand has been called on a Schedule it should no longer be considered as a fringe schedule and thus be
     * removed from the Priority Queue. All generated schedules are by definition fringe and, unless they have already
     * been created elsewhere, should be inserted into the Priority Queue.
     *
     * @return : A list of new fringe schedules for insertion into the Priority Queue, assuming they don't already
     * exist.
     */
    @Override
    public List<Schedule> expand() {

        // Check if we have found the optimal solution
        if (this.freeNodes.size() == 0) {
            return null; // TODO... Find a better way of indicating success than passing null
        }

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

                Map<Short, TaskNode> newFreeNodes = copyFreeNodesHook();
                Map<Short, TaskNode> newTaskNodes = copyTaskNodesHook();
                // Inserted task no longer needs to be stored
                newFreeNodes.remove(freeTask.getTaskID());
                newTaskNodes.remove(freeTask.getTaskID());

                // Iterating through all changed children
                int[][] changedChildLinks = freeTask.getChildLinks();

                for (int[] childLink: changedChildLinks) {
                    // Get old version of changed child from own storage or from original if not in own
                    TaskNode changedChild = this.getTaskNode((short)childLink[0]);
                    // Generate array of prerequisite locations
                    fillProcessorPrerequisites(
                            insertLocation,
                            processorID,
                            freeTask.getTaskCost(),
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
                        generateStateChange(freeTask.getTaskID(), processorID, insertLocation),
                        newTaskNodes,
                        newFreeNodes,
                        newProcessors,
                        Math.max(this.maxBottomLevel, freeTask.getBottomLevel() + insertLocation),
                        Math.max(this.maxDataReadyTime, insertLocation + freeTask.getTaskCost() + freeTask.getMaxCommunicationCost())
                ));
            }
        }
        return expandedSchedules;
    }

    /**
     * Private helper method for generating processor prerequisite arrays.
     *
     * @param insertPoint : The location in the processor that the parent task was inserted.
     * @param processorID : The ID of the processor that the parent task was inserted into.
     * @param taskLength : The length/cost of the parent task.
     * @param communicationCost : The cost of communicating the data from the parent to a specific child on a SEPERATE
     *                            processor.
     * @param arrayToFill : The array of processors prerequisite locations, dictated by the communication from one parent
     *                      to a specific child, that this method is filling.
     */
    private static void fillProcessorPrerequisites(int insertPoint,
                                                   int processorID,
                                                   int taskLength,
                                                   int communicationCost,
                                                   int[] arrayToFill) {

        int taskEndPoint = insertPoint + taskLength;
        // Theoretically could be sped up by not double writing the value at index processorID.
        Arrays.fill(arrayToFill, taskEndPoint + communicationCost);
        arrayToFill[processorID] = taskEndPoint;
    }

    @Override
    public List<int[]> rebuildPath() {
        return this.change.rebuildSolutionPath();
    }

    // ----------- Getter Methods ------------

    /**
     * @return : The longest critical path extending from any scheduled path.
     */
    @Override
    public int getMaxBottomLevel() {
        return this.maxBottomLevel;
    }

    /**
     * @return : The earliest moment that all data sent by currently scheduled tasks is first available on all stored
     * processors.
     */
    @Override
    public int getMaxDataReadyTime() {
        return this.maxDataReadyTime;
    }

    /**
     * @return : The sum total amount of time on each processor spent idling before all tasks (on all processors) are
     * completed.
     */
    @Override
    public int getIdleTime() {
        return this.processors.getIdleTime();
    }

    // ------------ Helper Methods ----------

    /**
     * Abstract helper method.
     * @return : The Original Schedule State that stores the static data not stored in the general Schedule States.
     */
    protected abstract OriginalScheduleState getOriginalSchedule();

    /**
     * Abstract helper method for generating ScheduleStateChange objects.
     *
     * @param freeTaskID : The ID of the task being inserted into the new schedule that the schedule state change
     *                     represents.
     * @param processorID : The ID of the processor that the task was inserted into.
     * @param insertLocation : The start location of the scheduled task.
     * @return : A new ScheduleStateChange object describing the change in state that occurs from inserting a specified
     *           task into a specified processor at a specified location.
     */
    protected abstract ScheduleStateChange generateStateChange(int freeTaskID, int processorID, int insertLocation);

    /**
     * Abstract helper method.
     * @param taskID : The ID of the task to return.
     * @return : TaskNode object that represents the current state of the Task with the given ID.
     */
    public abstract TaskNode getTaskNode(short taskID);

    /**
     * TODO...
     */
    protected abstract Map<Short, TaskNode> copyFreeNodesHook();

    protected abstract Map<Short, TaskNode> copyTaskNodesHook();
    /**
     * Generic Object.toString() override.
     * @return : String representation of the ScheduleState for use in debugging.
     */
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
}

