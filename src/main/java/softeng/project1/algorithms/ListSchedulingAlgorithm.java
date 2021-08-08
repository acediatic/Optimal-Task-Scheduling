package softeng.project1.algorithms;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.Graphs;
import softeng.project1.graph.processors.processor.ListProcessor;
import softeng.project1.graph.tasks.edges.ListCommunicationCost;
import softeng.project1.graph.tasks.ListTask;
import org.graphstream.algorithm.TopologicalSortDFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO...
 */
public class ListSchedulingAlgorithm implements SchedulingAlgorithm {

    private static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;  //default number of processors to begin with

    private final Graph graph; // Graph to process
    private final List<ListTask> tasksInTopology; // Need to be implemented from graph data
    private final ListProcessor[] processors;
    private ListCommunicationCost[][] communicationCosts; // Needs to be retrieved from graph
    private Map<Node, Integer> idMappings = new HashMap<Node, Integer>();


    public ListSchedulingAlgorithm(Graph read) {
        this(read, DEFAULT_NUMBER_OF_PROCESSORS);
    }

    public ListSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.tasksInTopology = new ArrayList<ListTask>(); // TODO... get from graph
        this.processors = new ListProcessor[numberOfProcessors];
        this.communicationCosts = new ListCommunicationCost[graph.getNodeCount()][]; // TODO... get from graph

        for (int i = 0; i<numberOfProcessors; i++) {
            processors[i] = (new ListProcessor(i));
        }

        graphToTaskAndCC();
    }

    // TODO... get these from Henry
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
            this.idMappings.put(sortedNodes.get(i), i);
        }

        //Converts edges into communication costs
        for(Node n: sortedNodes){

            communicationCosts[idMappings.get(n)] = new ListCommunicationCost[n.getOutDegree()];

            for(int i = 0; i < n.getOutDegree(); i++){

                ListTask currentTask = tasksInTopology.get(idMappings.get(n));

                Node child = n.getEdge(i).getNode1();
                ListTask childTask = tasksInTopology.get(idMappings.get(child));

                int communicationCost = (int) Double.parseDouble(n.getEdge(i).getAttribute("Weight").toString());
                communicationCosts[idMappings.get(n)][i] = new ListCommunicationCost(currentTask, childTask, communicationCost);

            }
        }
    }

    public Graph scheduleToGraph(List<int[]> schedule) {
        for(int[] task: schedule){
            int taskID = task[0];
            int processor = task[1];
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
        for(Node n: idMappings.keySet()){
            if(idMappings.get(n) == i){
                return n;
            }
        }
        return null;
    }

    private int getCostForProcessor(ListTask task, ListProcessor processor) {
        return Math.max(
                task.getPrerequisite(processor.getProcessorID()),
                processor.getOngoingTime()
        );
    }

    private void compareAndAdd (ListTask task) {
        int minID = 0;
        int earliestScheduleTime = Integer.MAX_VALUE;
        int earliestProcessorScheduleTime;

        // Greedily calculating the processor with the earliest possible schedule time
        for (ListProcessor processor: processors) {
            earliestProcessorScheduleTime = getCostForProcessor(task, processor);
            if (earliestProcessorScheduleTime < earliestScheduleTime) {
                earliestScheduleTime = earliestProcessorScheduleTime;
                minID = processor.getProcessorID();
            }
        }
        processors[minID].addTaskAtLocation(task, earliestScheduleTime);
        task.notifyChildren(minID, earliestScheduleTime, this.communicationCosts[task.getTaskID()]);
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

    private int getNodeWeight(Node n){
        return (int) Double.parseDouble(n.getAttribute("Weight").toString());
    }
}
