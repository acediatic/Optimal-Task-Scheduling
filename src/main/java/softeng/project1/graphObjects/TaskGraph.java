package softeng.project1.graphObjects;

import java.util.List;

public interface TaskGraph {

    public List<TaskNode> getFreeTasks();

    public TaskNode getTask(String taskName);

    @Override
    public String toString();
    
}