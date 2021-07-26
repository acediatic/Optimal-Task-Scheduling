package softeng.project1.graph;

import java.util.List;

/**
 * TODO...
 */
public interface TaskGraph {

    List<TaskNode> getFreeTasks();

    TaskNode getTask(String taskName);

    TaskGraph getOriginalTaskGraph();

    @Override
    String toString();
}
