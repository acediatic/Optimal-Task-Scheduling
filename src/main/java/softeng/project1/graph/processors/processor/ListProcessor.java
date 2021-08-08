package softeng.project1.graph.processors.processor;

import softeng.project1.graph.tasks.ListTask;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO...
 */
public class ListProcessor {

    private final int processorID;
    private final List<ListTask> tasks;
    private int ongoingTime;

    public ListProcessor(int processorID) {
        this.processorID = processorID;
        this.tasks = new ArrayList<>();
    }

    public void addTaskAtLocation(ListTask task, int insertPoint) {
        tasks.add(task);
        this.ongoingTime = insertPoint + task.getWeight();
    }

    public int getProcessorID() {
        return this.processorID;
    }

    public int getOngoingTime() {
        return this.ongoingTime;
    }

}
