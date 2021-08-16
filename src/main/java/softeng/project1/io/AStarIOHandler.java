package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;


    private final InputStream inputFileStream;
    private final String outputFilePath;
    private final int numProcessors; // Kinda cursed that this has to be here tbh
    private final String graphName;
    private Map<Integer, String> taskNames;

    public AStarIOHandler(String inputFilePath, String outputFilePath, String graphName, int numProcessors) {

        // TODO... sanitise inputs, check accessibility etc.
        try {
            this.inputFileStream = new FileInputStream(inputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(); // TODO... fix this
        }

        this.outputFilePath = outputFilePath;
        this.graphName = graphName;
        this.numProcessors = numProcessors;

    }

    @Override
    public Schedule readFile() {
        Map<Integer, TaskNode> taskNodeMap = new HashMap<>();
        Map<Integer, TaskNode> freeTaskNodeMap = new HashMap<>();

        Graph graphStreamInput = IOHelper.readFileAsGraphStream(this.inputFileStream);
        this.taskNames = IOHelper.mapTaskNamesToIDs(graphStreamInput);
        int numTasks = graphStreamInput.getNodeCount();

        Node task;
        OriginalTaskNodeState originalTaskNode;
        int newTaskID;

        for (int i = 0; i < numTasks; i++) {

            task = graphStreamInput.getNode(i);
            // Can this be avoided by assuming that the retrieval order is the same?
            newTaskID = getKeyFromTaskName(task.getId());

            originalTaskNode = new OriginalTaskNodeState(
                    newTaskID,
                    IOHelper.getProcessingCost(task),
                    IOHelper.getNumParents(task),
                    buildChildLinkArrays(task),
                    this.numProcessors
            );

            taskNodeMap.put(newTaskID, originalTaskNode);
            if (originalTaskNode.isFree()) {
                freeTaskNodeMap.put(newTaskID, originalTaskNode);
            }
        }

        // Calculate Bottom Levels
        setBottomLevels(taskNodeMap);

        return new OriginalScheduleState(taskNodeMap, freeTaskNodeMap, this.numProcessors);

    }

    /*
     * Calculates and sets the bottom level of each of the original task nodes.
     */
    public void setBottomLevels(Map<Integer, TaskNode> taskNodeMap) {
        int numNodes = taskNodeMap.size();
        int[] bottomLevels = new int[numNodes];

        // dynamically calculate all the bottom levels
        for (int i = 0; i < numNodes; i++) {
            dynamicBottomLevelCalculation(taskNodeMap, bottomLevels, i);
        }

        // Set the bottom level for each of the original task nodes.
        for (int i = 0; i < numNodes; i++) {
            OriginalTaskNodeState ogNode = (OriginalTaskNodeState) taskNodeMap.get(i);
            ogNode.setBottomLevel(bottomLevels[i]);
        }
    }

    /*
     * A recursive, dynamic solution to calculating the bottom levels of each of the nodes.
     */
    private int dynamicBottomLevelCalculation(Map<Integer, TaskNode> taskNodeMap, int[] bottomLevels, int nodeID) {
        // 0 indicates result has not yet been calculated
        if (bottomLevels[nodeID] == 0) {
            // get corresponding node
            TaskNode node = taskNodeMap.get(nodeID);
            int numChildren = node.getChildLinks().length;

            // If the node has children, it is not the base case, so recursively determine bottom level for children.
            if (numChildren > 0) {
                int currentMax = 0;
                for (int i = 0; i < numChildren; i++) {
                    int childTaskId = node.getChildLinks()[i][0];
                    currentMax = Math.max(currentMax, dynamicBottomLevelCalculation(taskNodeMap, bottomLevels, childTaskId));
                }
            } else {
                // base-case: node doesn't have children
                ; // do nothing.
            }

            // either way, this task's cost forms part of its bottom level.
            bottomLevels[nodeID] += node.getTaskCost();
        }
        // return this node's bottom level
        return bottomLevels[nodeID];
    }


    @Override
    public void writeFile(List<int[]> scheduledTaskData) {
        // TODO...
    }

    private int[][] buildChildLinkArrays(Node task) {

        Edge[] childLinks = IOHelper.getChildLinks(task);
        int[][] childLinkArrays = new int[childLinks.length][NUM_CHILD_LINK_DATA_FIELDS];

        Edge childLink;
        for (int i = 0; i < childLinks.length; i++) {
            childLink = childLinks[i];

            childLinkArrays[i] = new int[]{
                    getKeyFromTaskName(childLink.getId()),
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

        throw new RuntimeException(); // TODO... add better error handling
    }


}
