package softeng.project1.io;

import org.graphstream.graph.Graph;
import softeng.project1.graph.tasks.TaskNode;

import java.util.List;

public interface IOHandler {

    List<TaskNode> readFile();

    void writeFile(List<int[]> scheduledTaskData);

}
