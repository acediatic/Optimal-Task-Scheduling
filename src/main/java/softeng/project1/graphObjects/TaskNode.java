package softeng.project1.graphObjects;

import java.util.List;


/**
 * Interface describing a specific TaskNode in the graph representation of the scheduling problem. 
 * Instances of TaskNode should be immutable and final so that they can be accessed by multiple threads at once.
 */
public interface TaskNode {

    public String getTaskName();

    public int getTaskCost();

    public List<TaskPrerequisite> getParentTaskEdges();
    
    public List<TaskPrerequisite> getChildTaskEdges();

    public TaskNode getOriginalTaskNode();

    @Override
    public String toString();

}
