package softeng.project1.graphObjects;

public interface TaskPrerequisite {

    public int getCommunicationCost();
    
    public TaskNode getParentTask();

    public TaskNode getChildTask();

    @Override
    public String toString();

}
