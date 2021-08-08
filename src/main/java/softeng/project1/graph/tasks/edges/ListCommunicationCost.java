package softeng.project1.graph.tasks.edges;

import softeng.project1.graph.tasks.ListTask;

/**
 * TODO...
 */
public class ListCommunicationCost {

    private final ListTask taskFrom;
    private final ListTask taskTo;
    private final int cost;

    public ListCommunicationCost(ListTask taskFrom, ListTask taskTo, int cost) {
        this.taskFrom = taskFrom;
        this.taskTo = taskTo;
        this.cost = cost;
    }

    public int getCost() {
        return this.cost;
    }

    public ListTask getChildTask() {
        return this.taskTo;
    }


}
