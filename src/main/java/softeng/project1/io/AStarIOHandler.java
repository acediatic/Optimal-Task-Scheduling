package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;
import softeng.project1.algorithms.astar.heuristics.AlgorithmStep;
import softeng.project1.algorithms.astar.heuristics.ListSchedulingAlgorithmStep;
import softeng.project1.algorithms.valid.ListSchedulingAlgorithm;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.ComparableTask;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.*;
import java.util.*;

import static softeng.project1.io.IOHelper.getSortedNodes;

public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final InputStream inputFileStream;
    private final OutputStream outputStream;
    private final short numProcessors; // Kinda cursed that this has to be here tbh
    private final String graphName;
    private int sumTaskWeights = 0;
    private Graph graph;
    private AlgorithmStep listScheduleAlgoStep;
    private List<Node> sortedNodes;
    private Map<Node, Short> nodeShortMap;

    public AStarIOHandler(String inputFilePath, String outputFilePath, String graphName, short numProcessors) {

        // TODO... sanitise inputs, check accessibility etc.
        try {
            this.inputFileStream = new FileInputStream(inputFilePath);
            this.outputStream = new FileOutputStream(outputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(); // TODO... fix this
        }
        this.graphName = graphName;
        this.numProcessors = numProcessors;
        this.nodeShortMap = new HashMap<>();
    }

    private void createNodeShortMap() {
        for (short i = 0; i < sortedNodes.size(); i++) {
            nodeShortMap.put(sortedNodes.get(i), i);
        }
    }

    /**
     * Equivalent children are tasks that have the same:
     * 1. Weight
     * 2. Parents
     * 3. Children
     * 4. Parent Communication Weights
     * 5. Child Communication Weights
     * <p>
     * By forcing an ordering between them, we can cut down the search space
     * As otherwise, there's a different schedule created for all the permutations
     * of these equivalent tasks.
     */
    private void createEdgesBetweenEquivalentNodes() {
        int numTasks = this.graph.getNodeCount();

        ComparableTask[] nodeEqArr = new ComparableTask[numTasks];

        for (int i = 0; i < numTasks; i++) {
            Node task = this.graph.getNode(i);
            int taskWeight = IOHelper.getProcessingCost(task);
            ComparableTask ct = new ComparableTask(task, taskWeight);
            nodeEqArr[i] = ct;
        }

        // Sorted (log(n)), means we only have to compare with our neighbour.
        Arrays.sort(nodeEqArr);

        // compare for equality in O(n) (rather than O(n^2))
        for (int i = 0; i < nodeEqArr.length - 1; i++) {
            if (nodeEqArr[i].compareTo(nodeEqArr[i + 1]) == 0) {
                String edgeID = UUID.randomUUID().toString();
                graph.addEdge(edgeID, nodeEqArr[i].getTask(), nodeEqArr[i + 1].getTask(), true);
                graph.getEdge(edgeID).setAttribute(PROCESSING_COST_ATTRIBUTE_KEY, (double) 0);
            }
        }

    }

    private void createListSchedulingAlgoStep(List<Node> sortedNodes) {
        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(graph, numProcessors, sortedNodes);
        List<int[]> listSchedulePath = listScheduler.generateSchedule();
        int listScheduleEnd = listScheduler.getEndTime();

        listScheduleAlgoStep = new ListSchedulingAlgorithmStep(listScheduleEnd, listSchedulePath);
    }

    public AlgorithmStep getListSchedulingAlgoStep() {
        return listScheduleAlgoStep;
    }

    @Override
    public Schedule readFile() {
        Map<Short, TaskNode> taskNodeMap = new HashMap<>();
        Map<Short, TaskNode> freeTaskNodeMap = new HashMap<>();

        this.graph = IOHelper.readFileAsGraphStream(this.inputFileStream);
        int numTasks = this.graph.getNodeCount();

        // Check for and create edges between equivalent children
        createEdgesBetweenEquivalentNodes();

        sortedNodes = getSortedNodes(graph);

        createNodeShortMap();

        // Uses a list scheduling to calculate the best heuristic, and use it to prune A*.
        createListSchedulingAlgoStep(sortedNodes);

        Node task;
        OriginalTaskNodeState originalTaskNode;
        int taskWeight;

        for (short i = 0; i < numTasks; i++) {

            task = sortedNodes.get(i);

            taskWeight = IOHelper.getProcessingCost(task);
            this.sumTaskWeights += taskWeight;

            originalTaskNode = new OriginalTaskNodeState(
                    i,
                    taskWeight,
                    IOHelper.getNumParents(task),
                    buildChildLinkArrays(task),
                    IOHelper.dynamicBottomLevelCalculation(task),
                    this.numProcessors
            );

            taskNodeMap.put(i, originalTaskNode);
            if (originalTaskNode.isFree()) {
                freeTaskNodeMap.put(i, originalTaskNode);
            }
        }

        return new OriginalScheduleState(
                taskNodeMap,
                freeTaskNodeMap,
                this.numProcessors,
                IOHelper.getBranchingFactor(this.graph)
        );

    }

    @Override
    public String writeFile(List<int[]> scheduledTaskData) {


        for (int[] scheduling : scheduledTaskData) {
            IOHelper.addSchedulingToTask(
                    this.sortedNodes.get(scheduling[0]), // Get name from task ID
                    scheduling);
        }

        FileSinkDOT fileSink = new FileSinkDOT(true);

        try {
            fileSink.writeAll(this.graph, this.outputStream);
        } catch (IOException e) {
            e.printStackTrace(); // TODO...
            throw new RuntimeException();
        }

        return "Max processor length: " + getScheduleMaxLength(scheduledTaskData);
    }

    @Override
    public int getSumWeights() {
        return this.sumTaskWeights;
    }

    private int[][] buildChildLinkArrays(Node task) {

        Edge[] childLinks = IOHelper.getChildLinks(task);
        int[][] childLinkArrays = new int[childLinks.length][NUM_CHILD_LINK_DATA_FIELDS];

        Edge childLink;
        for (int i = 0; i < childLinks.length; i++) {
            childLink = childLinks[i];

            childLinkArrays[i] = new int[]{
                    nodeShortMap.get(childLink.getTargetNode()),
                    IOHelper.getProcessingCost(childLink)
            };
        }
        return childLinkArrays;
    }

    private int getScheduleMaxLength(List<int[]> scheduleLocations) {
        int maxLength = 0;

        for (int[] scheduleLocation : scheduleLocations) {
            int scheduleLastPoint = IOHelper.getProcessingCost(sortedNodes.get(scheduleLocation[0])) + scheduleLocation[2];
            if (scheduleLastPoint > maxLength) {
                maxLength = scheduleLastPoint;
            }

        }
        return maxLength;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public List<Node> getListSortedNodes() {
        return sortedNodes;
    }
}
