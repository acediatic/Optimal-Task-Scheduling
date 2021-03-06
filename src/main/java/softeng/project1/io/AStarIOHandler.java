package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
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

/**
 * A class responsible for the handling of input and output for the A* scheduling algorithm
 */
public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final InputStream inputFileStream;
    private final short numProcessors;
    private final Map<Node, Short> nodeShortMap;
    private final boolean isVisual;
    private final String outputPath;
    private final String graphName;
    private int sumTaskWeights = 0;
    private Graph graph;
    private Graph uneditedGraph;
    private AlgorithmStep listScheduleAlgoStep;
    private List<Node> sortedNodes;
    private List<String> editedEdges;

    public AStarIOHandler(String inputFilePath, String outputFilePath, short numProcessors, String graphName, boolean isVisual) {
        try {
            this.inputFileStream = new FileInputStream(inputFilePath);
            outputPath = outputFilePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        this.numProcessors = numProcessors;
        this.nodeShortMap = new HashMap<>();
        this.graphName = graphName;
        this.isVisual = isVisual;
    }


    /**
     * Reads a dot file from disk and parses it into the format usable by the algorithm
     *
     * @return the original schedule state, upon which the algorithm can start.
     */
    @Override
    public Schedule readFile() {
        Map<Short, TaskNode> taskNodeMap = new HashMap<>();
        Map<Short, TaskNode> freeTaskNodeMap = new HashMap<>();

        this.graph = IOHelper.readFileAsGraphStream(this.inputFileStream);
        // creates a duplicate graph that can be used by the GUI
        if (isVisual) {
            this.uneditedGraph = Graphs.clone(this.graph);
        }
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

    /**
     * The list scheduling algorithm step is used to prune schedules that are not better than the list scheduling.
     * This method creates the list schedule for this purpose.
     *
     * @param sortedNodes
     */
    private void createListSchedulingAlgoStep(List<Node> sortedNodes) {
        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(graph, numProcessors, sortedNodes);
        List<int[]> listSchedulePath = listScheduler.generateSchedule();
        int listScheduleEnd = listScheduler.getEndTime();

        listScheduleAlgoStep = new ListSchedulingAlgorithmStep(listScheduleEnd, listSchedulePath);
    }

    /**
     * @return the algorithm step for the list scheduling object.
     */
    public AlgorithmStep getListSchedulingAlgoStep() {
        return listScheduleAlgoStep;
    }

    /**
     * A utility method that creates a mapping of nodes to their
     * short representation, for O(1) lookup.
     */
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
        editedEdges = new LinkedList<>();
        // compare for equality in O(n) (rather than O(n^2))
        for (int i = 0; i < nodeEqArr.length - 1; i++) {
            if (nodeEqArr[i].compareTo(nodeEqArr[i + 1]) == 0) {
                String edgeID = UUID.randomUUID().toString();
                editedEdges.add(edgeID);
                graph.addEdge(edgeID, nodeEqArr[i].getTask(), nodeEqArr[i + 1].getTask(), true);
                graph.getEdge(edgeID).setAttribute(PROCESSING_COST_ATTRIBUTE_KEY, (double) 0);
            }
        }

    }

    /**
     * Writes the optimal schedule back to a dot file.
     *
     * @param scheduledTaskData the optimal schedule
     * @return a string representing the final details of the schedule
     */
    @Override
    public String writeFile(List<int[]> scheduledTaskData) {


        for (int[] scheduling : scheduledTaskData) {
            IOHelper.addSchedulingToTask(
                    this.sortedNodes.get(scheduling[0]), // Get name from task ID
                    scheduling);
        }

        for (String edgeID : editedEdges) {
            this.graph.removeEdge(edgeID);
        }

        FileSinkDOT fileSink = new FileSinkDOT(true);

        // renaming the dot file and adding the graph name to the first line.
        try {
            String tempOutputName = outputPath.substring(0, outputPath.length() - 4) + "tempOutput.dot";
            FileOutputStream outputStream = new FileOutputStream(tempOutputName);
            fileSink.writeAll(this.graph, outputStream);

            // setup new file with correct name, and reader on temp file with graph info.
            File oldOutput = new File(tempOutputName);
            File newOutput = new File(outputPath);
            BufferedReader reader = new BufferedReader(new FileReader(oldOutput));
            BufferedWriter output = new BufferedWriter(new FileWriter(newOutput));

            // burn first line, to replace.
            reader.readLine();

            // Add new first line (name is surrounded in quotation marks)
            output.write("digraph \"" + graphName + "-output\" {\n");

            // replace rest of file
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append('\n');
            }
            output.close();
            reader.close();

            // delete original (temp) file
            if (!oldOutput.delete()) {
                throw new IOException();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return "Max processor length: " + getScheduleMaxLength(scheduledTaskData);
    }

    /**
     * @return the sum of all task weights. Used by the heuristic.
     */
    @Override
    public int getSumWeights() {
        return this.sumTaskWeights;
    }

    /**
     * Creates the link arrays that represent dependencies between children and their
     * parents, with the associated commuincation costs.
     *
     * @param task the parent task
     * @return the array of children arrays representing links
     */
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

    /**
     * Determines the maximum length of the optimal schedule
     *
     * @param scheduleLocations the optimal schedule
     * @return the length of this schedule
     */
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

    /**
     * @return the unedited input graph for use by the GUI
     */
    public Graph getGraph() {
        return this.uneditedGraph;
    }

    /**
     * @return the topologically sorted list of nodes, representing tasks
     */
    public List<Node> getListSortedNodes() {
        return sortedNodes;
    }
}
