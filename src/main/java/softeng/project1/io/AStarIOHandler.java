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
        int numTasks = graphStreamInput.getNodeCount();

        this.taskNames = IOHelper.mapTaskNamesToIDs(graphStreamInput);

        Node task;
        Edge childLink;

        for (int i = 0; i < numTasks; i++) {

            task = graphStreamInput.getNode(i);

            returnList.add(new OriginalTaskNodeState(
                    i,
                    IOHelper.getProcessingCost(task),
                    IOHelper.getNumParents(task),
                    buildChildLinkArrays(task),
                    IOHelper.calculateBottomLevel(task),
                    this.numProcessors
            ));
        }
    }

    @Override
    public void writeFile(List<int[]> scheduledTaskData) {
        // TODO...
    }

    private int[][] buildChildLinkArrays(Node task) {
        return null; // TODO...
    }



}
