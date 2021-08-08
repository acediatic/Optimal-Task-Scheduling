package softeng.project1.algorithms;

import org.graphstream.graph.Graph;
import softeng.project1.graph.processors.processor.ListProcessor;
import softeng.project1.graph.tasks.edges.ListCommunicationCost;
import softeng.project1.graph.tasks.ListTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Osama Kashif, Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Valid but incomplete and non-optimal implementation of Scheduling Algorithm.
 * List Scheduling Algorithm works by first ordering all nodes topologically, then greedily inserting nodes into
 * the earliest possible location available on any processor.
 *
 * ListSchedulingAlgorithm returns a list of int[] with each int[] containing the location of a scheduled task.
 * Specifically:
 * index 0 - Task ID
 * index 1 - Processor ID
 * index 2 - Task's scheduled start point in processor
 * The list is ordered topologically but should be treated as though it is ordered arbitrarily as specific ordering
 * depends on the order of the initial inputted list and thus is inconsistent.
 */
public class ListSchedulingAlgorithm implements SchedulingAlgorithm {

    private static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;  //default number of processors to begin with

    private final Graph graph; // Graph to process
    private final List<ListTask> tasksInTopology; // Need to be implemented from graph data
    private final ListProcessor[] processors;
    private final ListCommunicationCost[][] communicationCosts; // Needs to be retrieved from graph


    public ListSchedulingAlgorithm(Graph read) {
        this(read, DEFAULT_NUMBER_OF_PROCESSORS);
    }

    public ListSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.tasksInTopology = null; // TODO... get from graph
        this.processors = new ListProcessor[numberOfProcessors];
        this.communicationCosts = null; // TODO... get from graph

        for (int i = 0; i<numberOfProcessors; i++) {
            processors[i] = (new ListProcessor(i));
        }
    }

    @Override
    public List<int[]> generateSchedule() {
        List<int[]> returnList = new ArrayList<>();

        for (ListTask task: tasksInTopology) {
            compareAndAdd(task);
            returnList.add(task.getAsIntArray());
        }
        return returnList;
    }

    // TODO... get these from Henry
    public void graphToTaskAndCC() {

    }

    public void scheduleToGraph() {

    }

    /**
     * Greedily calculates the earliest possible location on any processor that the task can be inserted and then does
     * so. Updating of stored data in Processors and Tasks to accommodate scheduling is automatically called from the
     * method.
     * Method assumes the following:
     * - Given Task is 'free' (all parent tasks have already been scheduled).
     * - Number of processors is at least 1.
     * - Task is not null.
     *
     * @param task: Free ListTask object to be greedily scheduled into the processors.
     */
    private void compareAndAdd (ListTask task) {
        int bestProcessorID = 0;
        int earliestScheduleTime = Integer.MAX_VALUE;
        int earliestProcessorScheduleTime;

        // Greedily calculating the processor with the earliest possible schedule time
        for (ListProcessor processor: processors) {
            earliestProcessorScheduleTime = getCostForProcessor(task, processor);
            if (earliestProcessorScheduleTime < earliestScheduleTime) {
                earliestScheduleTime = earliestProcessorScheduleTime;
                bestProcessorID = processor.getProcessorID();
            }
        }
        // Updating stored Processor values
        processors[bestProcessorID].addTaskAtLocation(task, earliestScheduleTime);
        // Updating stored Task values
        task.notifyChildren(bestProcessorID, earliestScheduleTime, this.communicationCosts[task.getTaskID()]);
    }

    private static int getCostForProcessor(ListTask task, ListProcessor processor) {
        return Math.max(
                task.getPrerequisite(processor.getProcessorID()),
                processor.getOngoingTime()
        );
    }
}
