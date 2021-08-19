package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.ComparableTask;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.*;
import java.util.*;

public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private final InputStream inputFileStream;
    private final OutputStream outputStream;
    private final short numProcessors; // Kinda cursed that this has to be here tbh
    private final String graphName;
    private Map<Short, String> taskNames;
    private int sumTaskWeights = 0;
    private Graph graphStreamInput;

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
    }

    /**
     * Equivalent children are tasks that have the same:
     * 1. Weight
     * 2. Parents
     * 3. Children
     * 4. Parent Communication Weights
     * 5. Child Communication Weights
     *
     * By forcing an ordering between them, we can cut down the search space
     * As otherwise, there's a different schedule created for all the permutations
     * of these equivalent tasks.
     */
    private void createEdgesBetweenEquivalentNodes() {
        int numTasks = this.graphStreamInput.getNodeCount();

        ComparableTask[] nodeEqArr = new ComparableTask[numTasks];

        for (int i = 0; i < numTasks; i++) {
            Node task = this.graphStreamInput.getNode(i);
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
                graphStreamInput.addEdge(edgeID, nodeEqArr[i].getTask(), nodeEqArr[i + 1].getTask(), true);
                graphStreamInput.getEdge(edgeID).setAttribute(PROCESSING_COST_ATTRIBUTE_KEY, (double) 0);
            }
        }

    }

    @Override
    public Schedule readFile() {
        Map<Short, TaskNode> taskNodeMap = new HashMap<>();
        Map<Short, TaskNode> freeTaskNodeMap = new HashMap<>();

        this.graphStreamInput = IOHelper.readFileAsGraphStream(this.inputFileStream);
        this.taskNames = IOHelper.mapTaskNamesToIDs(this.graphStreamInput);
        int numTasks = this.graphStreamInput.getNodeCount();

        // Check for and create edges between equivalent children
        createEdgesBetweenEquivalentNodes();

        Node task;
        OriginalTaskNodeState originalTaskNode;
        short newTaskID;
        int taskWeight;

        for (int i = 0; i < numTasks; i++) {

            task = this.graphStreamInput.getNode(i);
            // Can this be avoided by assuming that the retrieval order is the same?
            newTaskID = getKeyFromTaskName(task.getId());

            taskWeight = IOHelper.getProcessingCost(task);
            this.sumTaskWeights += taskWeight;

            originalTaskNode = new OriginalTaskNodeState(
                    newTaskID,
                    taskWeight,
                    IOHelper.getNumParents(task),
                    buildChildLinkArrays(task),
                    IOHelper.dynamicBottomLevelCalculation(task),
                    this.numProcessors
            );

            taskNodeMap.put(newTaskID, originalTaskNode);
            if (originalTaskNode.isFree()) {
                freeTaskNodeMap.put(newTaskID, originalTaskNode);
            }
        }

        return new OriginalScheduleState(
                taskNodeMap,
                freeTaskNodeMap,
                this.numProcessors,
                IOHelper.getBranchingFactor(this.graphStreamInput)
        );

    }

    @Override
    public String writeFile(List<int[]> scheduledTaskData) {


        for (int[] scheduling : scheduledTaskData) {
            IOHelper.addSchedulingToTask(
                    this.graphStreamInput.getNode(
                            this.taskNames.get((short) scheduling[0]) // Get name from task ID
                    ),
                    scheduling);
        }

        FileSinkDOT fileSink = new FileSinkDOT(true);

        try {
            fileSink.writeAll(this.graphStreamInput, this.outputStream);
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
                    getKeyFromTaskName(childLink.getTargetNode().getId()),
                    IOHelper.getProcessingCost(childLink)
            };
        }
        return childLinkArrays;
    }

    private short getKeyFromTaskName(String taskName) {

        // Lazy O(n) search for backwards value -> key mapping. Importing a bi-map would make this better
        for (Map.Entry<Short, String> taskNameIDMapping : this.taskNames.entrySet()) {
            if (taskNameIDMapping.getValue().equals(taskName)) {
                return taskNameIDMapping.getKey();
            }
        }

        throw new RuntimeException("Failed to find Task: " + taskName + " in task ID -> name map"); // TODO... add better error handling
    }

    private int getScheduleMaxLength(List<int[]> scheduleLocations) {
        int maxLength = 0;

        for (int[] scheduleLocation: scheduleLocations) {
            int scheduleLastPoint = IOHelper.getProcessingCost(this.graphStreamInput.getNode(this.taskNames.get((short) scheduleLocation[0]))) + scheduleLocation[2];
            if (scheduleLastPoint > maxLength) {
                maxLength = scheduleLastPoint;
            }

        }
        return maxLength;
    }

    public String getTaskName(short taskID){
        return this.taskNames.get(taskID);
    }

    public Graph getGraph(){
        return this.graphStreamInput;
    }


}
