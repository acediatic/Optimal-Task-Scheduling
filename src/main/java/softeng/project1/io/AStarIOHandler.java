package softeng.project1.io;

import softeng.project1.graph.tasks.TaskNode;

import java.util.List;

public class AStarIOHandler implements IOHandler {

    private final String inputFilePath;
    private final String outputFilePath;
    private String[] taskNames;

    public AStarIOHandler(String inputFilePath, String outputFilePath) {

        // TODO... sanitise inputs, check accessibility etc.
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;

    }

    @Override
    public List<TaskNode> readFile() {
        return null; // TODO...
    }

    @Override
    public void writeFile(List<int[]> scheduledTaskData) {
        // TODO...
    }



}
