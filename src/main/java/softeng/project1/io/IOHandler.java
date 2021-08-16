package softeng.project1.io;

import org.graphstream.graph.Graph;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.TaskNode;

import java.util.List;

public interface IOHandler {

    Schedule readFile();

    void writeFile(List<int[]> scheduledTaskData);

}
