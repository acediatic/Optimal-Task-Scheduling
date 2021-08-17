package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;


    private final InputStream inputFileStream;
    private final OutputStream outputStream;
    private final int numProcessors; // Kinda cursed that this has to be here tbh
    private final String graphName;
    private Map<Integer, String> taskNames;
    private int sumTaskWeights = 0;

    private Graph graphStreamInput;

    public AStarIOHandler(String inputFilePath, String outputFilePath, String graphName, int numProcessors) {

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

    @Override
    public Schedule readFile() {
        Map<Integer, TaskNode> taskNodeMap = new HashMap<>();
        Map<Integer, TaskNode> freeTaskNodeMap = new HashMap<>();

        this.graphStreamInput = IOHelper.readFileAsGraphStream(this.inputFileStream);
        this.taskNames = IOHelper.mapTaskNamesToIDs(this.graphStreamInput);
        int numTasks = this.graphStreamInput.getNodeCount();

        Node task;
        OriginalTaskNodeState originalTaskNode;
        int newTaskID;
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

        return new OriginalScheduleState(taskNodeMap, freeTaskNodeMap, this.numProcessors);

    }

    @Override
    public void writeFile(List<int[]> scheduledTaskData) {


        for (int[] scheduling: scheduledTaskData) {
            IOHelper.addSchedulingToTask(
                    this.graphStreamInput.getNode(
                            this.taskNames.get(scheduling[0]) // Get name from task ID
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

    private int getKeyFromTaskName(String taskName) {

        // Lazy O(n) search for backwards value -> key mapping. Importing a bi-map would make this better
        for (Map.Entry<Integer, String> taskNameIDMapping : this.taskNames.entrySet()) {
            if (taskNameIDMapping.getValue().equals(taskName)) {
                return taskNameIDMapping.getKey();
            }
        }

        throw new RuntimeException("Failed to find Task: " + taskName + " in task ID -> name map"); // TODO... add better error handling
    }




}
