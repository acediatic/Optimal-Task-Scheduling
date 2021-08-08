package softeng.project1.algorithms;

import org.graphstream.graph.Graph;
import softeng.project1.graph.processors.processor.ListProcessor;
import softeng.project1.graph.tasks.edges.ListCommunicationCost;
import softeng.project1.graph.tasks.ListTask;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 */
public class ListSchedulingAlgorithm implements SchedulingAlgorithm {

    private static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;  //default number of processors to begin with

    private final Graph graph; // Graph to process
    private final List<ListTask> tasksInTopology; // Need to be implemented from graph data
    private final ListProcessor[] processors;
    private final ListCommunicationCost[][] communicationCosts; // Needs to be retrieved from graph


    public ListSchedulingAlgorithm(Graph read) {
        this(read, DEFAULT_NUMBER_OF_PROCESSORS);
    }

    public ListSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.tasksInTopology = null; // TODO... get from graph
        this.processors = new ListProcessor[numberOfProcessors];
        this.communicationCosts = null; // TODO... get from graph

        for (int i = 0; i<numberOfProcessors; i++) {
            processors[i] = (new ListProcessor(i));
        }
    }

    // TODO... get these from Henry
    public void graphToTaskAndCC() {

    }

    public void scheduleToGraph() {

    }

    private int getCostForProcessor(ListTask task, ListProcessor processor) {
        return Math.max(
                task.getPrerequisite(processor.getProcessorID()),
                processor.getOngoingTime()
        );
    }

    private void compareAndAdd (ListTask task) {
        int minID = 0;
        int earliestScheduleTime = Integer.MAX_VALUE;
        int earliestProcessorScheduleTime;

        // Greedily calculating the processor with the earliest possible schedule time
        for (ListProcessor processor: processors) {
            earliestProcessorScheduleTime = getCostForProcessor(task, processor);
            if (earliestProcessorScheduleTime < earliestScheduleTime) {
                earliestScheduleTime = earliestProcessorScheduleTime;
                minID = processor.getProcessorID();
            }
        }
        processors[minID].addTaskAtLocation(task, earliestScheduleTime);
        task.notifyChildren(minID, earliestScheduleTime, this.communicationCosts[task.getTaskID()]);
    }

    @Override
    public List<int[]> generateSchedule() {
        List<int[]> returnList = new ArrayList<>();

        for (ListTask task: tasksInTopology) {
            compareAndAdd(task);
            returnList.add(task.getAsIntArray());
        }
      return returnList;
    }
}
