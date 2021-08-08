package softeng.project1.algorithms;

import org.graphstream.algorithm.TopologicalSortDFS;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.jetbrains.annotations.NotNull;
import softeng.project1.graph.processors.processor.ListProcessor;
import softeng.project1.graph.tasks.edges.ListCommunicationCost;
import softeng.project1.graph.tasks.ListTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Graph graph; // Graph to process
    private final List<ListTask> tasksInTopology; // Need to be implemented from graph data
    private final ListProcessor[] processors;
    private final ListCommunicationCost[][] communicationCosts; // Needs to be retrieved from graph
    private final Map<Node, Integer> nodeToIDMap;

    /**
     * TODO...
     */
    public ListSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.processors = new ListProcessor[numberOfProcessors];
        this.nodeToIDMap = new HashMap<Node, Integer>();
        for (int i = 0; i<numberOfProcessors; i++) {
            processors[i] = (new ListProcessor(i));
        }

        this.tasksInTopology = new ArrayList<>();
        this.communicationCosts = new ListCommunicationCost[read.getNodeCount()][];

        graphToTaskAndCC();
    }

    /**
     * Greedily generates a valid, non-optimal schedule for a set of tasks following the constraints described by the
     * multi-processor task scheduling problem with communication costs.
     * The schedule is calculated by iterating over a topologically ordered list of tasks and greedily scheduling each
     * task into the earliest possible location available at that moment.
     * The scheduler does not take into account the possibility of inserting tasks between other tasks.
     *
     * @return : A list of integer arrays describing the location of each task within the schedule.
     */
    @Override
    public List<int[]> generateSchedule() {
        List<int[]> returnList = new ArrayList<>();

        for (ListTask task: tasksInTopology) {
            compareAndAdd(task);
            returnList.add(task.getAsIntArray());
        }
        return returnList;
    }

    /**
     * Converts a graph object from graphstream into the relevant data structures
     * i.e. ListTask and ListCommunicationCost
     */
    private void graphToTaskAndCC() {
        //Sorts nodes into a topological ordering
        TopologicalSortDFS sorter = new TopologicalSortDFS();
        sorter.init(graph);
        sorter.compute();
        List<Node> sortedNodes = sorter.getSortedNodes();

        //Converts Nodes to ListTask
        for(int i = 0; i < sortedNodes.size(); i++){
            ListTask task = new ListTask(i, getNodeWeight(sortedNodes.get(i)), processors.length);
            this.tasksInTopology.add(task);
            this.nodeToIDMap.put(sortedNodes.get(i), i);
        }

        //Converts edges into communication costs
        for(Node n: sortedNodes){

            communicationCosts[nodeToIDMap.get(n)] = new ListCommunicationCost[n.getOutDegree()];

            for(int i = 0; i < n.getOutDegree(); i++){

                ListTask currentTask = tasksInTopology.get(nodeToIDMap.get(n));

                Node child = n.getLeavingEdge(i).getNode1();
                ListTask childTask = tasksInTopology.get(nodeToIDMap.get(child));

                int communicationCost = (int) Double.parseDouble(n.getLeavingEdge(i).getAttribute("Weight").toString());
                communicationCosts[nodeToIDMap.get(n)][i] = new ListCommunicationCost(currentTask, childTask, communicationCost);

            }
        }
    }

    /**
     * Converts a schedule into a graph object
     * @param schedule List of integer arrays where each entry in list is a task with integer array of properties
     * @return Graph object with updated attributes for nodes
     */
    public Graph scheduleToGraph(List<int[]> schedule) {
        for(int[] task: schedule){
            int taskID = task[0];
            int processor = task[1] + 1;
            int startTime = task[2];

            Node currentNode = findNode(taskID);

            currentNode.setAttribute("Weight", getNodeWeight(currentNode));
            currentNode.setAttribute("Start", startTime);
            currentNode.setAttribute("Processor", processor);
        }

        graph.edges().forEach(e ->{
            int edgeWeight = (int) Double.parseDouble(e.getAttribute("Weight").toString());
            e.setAttribute("Weight", edgeWeight);
        });

        return this.graph;
    }

    private Node findNode(int i){
        for(Node n: nodeToIDMap.keySet()){
            if(nodeToIDMap.get(n) == i){
                return n;
            }
        }
        return null;
    }

    /**
     * Greedily calculates the earliest possible location on any processor that the task can be inserted and then does
     * so. Updating of stored data in Processors and Tasks to accommodate scheduling is automatically called from the
     * method.
     * Method assumes the following:
     * - Given Task is 'free' (all parent tasks have already been scheduled).
     * - Number of processors is at least 1.
     *
     * @param task: Free ListTask object to be greedily scheduled into the processors.
     */
    private void compareAndAdd (@NotNull ListTask task) {
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

    /**
     * Calculates the earliest possible location in a processor that a specific task could be scheduled.
     *
     * @param task : Free ListTask to calculate the earliest possible insert location for.
     * @param processor : ListProcessor that the Task is being placed in.
     *
     * @return : Earliest/lowest possible time/location that the Task could be scheduled in the processor without
     *           breaking any constraints.
     */
    private static int getCostForProcessor(@NotNull ListTask task, @NotNull ListProcessor processor) {
        return Math.max(
                task.getPrerequisite(processor.getProcessorID()),
                processor.getOngoingTime()
        );
    }

    private int getNodeWeight(Node n){
        return (int) Double.parseDouble(n.getAttribute("Weight").toString());
    }
}
