package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AStarIOHandler implements IOHandler {

    private static final int NUM_CHILD_LINK_DATA_FIELDS = 2;


    private final InputStream inputFileStream;
    private final String outputFilePath;
    private final int numProcessors; // Kinda cursed that this has to be here tbh
    private Map<Integer, String> taskNames;

    public AStarIOHandler(String inputFilePath, String outputFilePath, int numProcessors) {

        // TODO... sanitise inputs, check accessibility etc.
        try {
            this.inputFileStream = new FileInputStream(inputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(); // TODO... fix this
        }

        this.outputFilePath = outputFilePath;
        this.numProcessors = numProcessors;

    }

    @Override
    public Schedule readFile() {
        List<TaskNode> taskList = new ArrayList<>();
        List<TaskNode> freeTaskList = new ArrayList<>();

        Graph graphStreamInput = IOHelper.readFileAsGraphStream(this.inputFileStream);
        this.taskNames = IOHelper.mapTaskNamesToIDs(graphStreamInput);
        int numTasks = graphStreamInput.getNodeCount();

        Node task;
        TaskNode originalTaskNode;
        for (int i = 0; i < numTasks; i++) {

            task = graphStreamInput.getNode(i);

            originalTaskNode = new OriginalTaskNodeState(
                    i,
                    IOHelper.getProcessingCost(task),
                    IOHelper.getNumParents(task),
                    buildChildLinkArrays(task),
                    IOHelper.calculateBottomLevel(task),
                    this.numProcessors
            );

            taskList.add(originalTaskNode);
            if (originalTaskNode.isFree()) {
                freeTaskList.add(originalTaskNode);
            }

        }
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
        for (Map.Entry<Integer, String> taskNameIDMapping: this.taskNames.entrySet()) {
            if (taskNameIDMapping.getValue().equals(taskName)) {
                return taskNameIDMapping.getKey();
            }
        }

        throw new RuntimeException(); // TODO... add better error handling
    }



}
