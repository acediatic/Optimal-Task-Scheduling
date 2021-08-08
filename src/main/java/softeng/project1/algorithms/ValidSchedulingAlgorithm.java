package softeng.project1.algorithms;

import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class ValidSchedulingAlgorithm implements SchedulingAlgorithm {

    private static final int DEFAULT_NUMBER_OF_PROCESSORS = 1;  //default number of processors to begin with

    private final Graph graph; // Graph to process
    private final int numberOfProcessors;
    private final List<Task> tasksInTopology; // Need to be implemented from graph data
    private final List<ValidProcessor> processors;
    private final CommunicationCost[][] communicationCosts; // Needs to be retrieved from graph


//    private List<Processors> processors = new ArrayList<Processors>();

    public ValidSchedulingAlgorithm(Graph read) {
        this(read, DEFAULT_NUMBER_OF_PROCESSORS);
    }

    public ValidSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.numberOfProcessors = numberOfProcessors;
        this.tasksInTopology = null; // TODO... get from graph
        this.processors = new ArrayList<>(numberOfProcessors);
        this.communicationCosts = null; // TODO... get from graph

        for (int i = 0; i<this.numberOfProcessors; i++) {
            processors.add(new ValidProcessor(i));
        }
    }

    public void graphToTaskAndCC() {

    }

    public void scheduleToGraph() {

    }

    private int getCostForProcessor(Task task, ValidProcessor processor) {
        return Math.max(
                task.getPrerequisite(processor.getId()),
                processor.getOngoingTime()
        );
    }

    private void compareAndAdd (Task task) {
        int minId = 0;
        int earliestScheduleTime = Integer.MAX_VALUE;
        int earliestProcessorScheduleTime;

        // Greedily calculating the processor with the earliest possible schedule time
        for (ValidProcessor processor: processors) {
            earliestProcessorScheduleTime = getCostForProcessor(task, processor);
            if (earliestProcessorScheduleTime < earliestScheduleTime) {
                earliestScheduleTime = earliestProcessorScheduleTime;
                minId = processor.getId();
            }
        }
        processors.get(minId).addTaskAtLocation(task, earliestScheduleTime);
    }

    public void schedule() {
        for (Task task: tasksInTopology) {
            compareAndAdd(task);
        }
    }

    private class ValidProcessor {
        private final int id;
        private final List<Task> tasks;
        private int ongoingTime;

        protected ValidProcessor(int id) {
            this.id = id;
            this.tasks = new ArrayList<>();
        }

        protected void addTaskAtLocation(Task task, int insertPoint) {
            task.assignStart(insertPoint); // Do this here?
            tasks.add(task);
            this.ongoingTime = insertPoint + task.getWeight();
        }

        protected int getId() {
            return this.id;
        }

        protected int getOngoingTime() {
            return this.ongoingTime;
        }

        protected boolean checkTaskIn(Task task) {
            return this.tasks.contains(task);
        }
    }
    private class Task {
        private int[] processorPrerequisites;
        private int weight;
        private int start;
        private String name;

        protected Task(String name, int weight) {
            this.weight = weight;
            this.name = name;
        }

        protected Task(String name, int weight, int start) {
            this.weight = weight;
            this.name = name;
            this.start = start;
        }

        protected void assignStart (int start) {
            this.start = start;
        }

        public int getWeight() {
            return this.weight;
        }

        protected int getStart() {
            return this.start;
        }

        public void notifyPrerequisiteFulfilled(int[] fulfilledPrerequisite) {
            for (int i = 0; i < this.processorPrerequisites.length; i++) {
                this.processorPrerequisites[i] = Math.max(this.processorPrerequisites[i], fulfilledPrerequisite[i]);
            }

        }

        protected int getPrerequisite(int processorID) {
            return this.processorPrerequisites[processorID];
        }

        protected int[] getPrerequisites() {
            return this.processorPrerequisites;
        }
    }

    private class CommunicationCost {
        private Task taskFrom;
        private Task taskTo;
        private int weight;
        protected CommunicationCost(Task taskFrom, Task taskTo, int weight) {
            this.taskFrom = taskFrom;
            this.taskTo = taskTo;
            this.weight = weight;
        }

        protected int tellCommunicationCost(Task taskFrom, Task taskTo) {
            if ((this.taskFrom == taskFrom) && (this.taskTo == taskTo)) {
                return this.weight;
            } else {
                return -1;
            }
        }
    }

}
