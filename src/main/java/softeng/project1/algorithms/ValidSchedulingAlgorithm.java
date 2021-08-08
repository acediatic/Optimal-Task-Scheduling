package softeng.project1.algorithms;

import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
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
        task.notifyChildren(minID, earliestScheduleTime, this.communicationCosts[task.getTaskID()]);
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

        private final int taskID;
        private final int weight;

        private int[] processorPrerequisites;
        private int start;

        protected Task(int taskID, int weight) {
            this.weight = weight;
            this.taskID = taskID;
        }

        protected Task(int taskID, int weight, int start) {
            this.weight = weight;
            this.taskID = taskID;
            this.start = start;
        }

        protected void assignStart (int start) {
            this.start = start;
        }

        public int getTaskID() {
            return this.taskID;
        }

        public int getWeight() {
            return this.weight;
        }

        protected int getStart() {
            return this.start;
        }

        protected void notifyChildren(int processorID, int insertPoint, CommunicationCost[] children) {

            int[] fulfilledPrerequisite = new int[this.processorPrerequisites.length];
            int taskEndPoint = insertPoint + this.weight;

            for (CommunicationCost communicationCost: children) {

                Arrays.fill(fulfilledPrerequisite, taskEndPoint + communicationCost.getCost());
                fulfilledPrerequisite[processorID] = taskEndPoint;

                communicationCost.getChildTask().notifyPrerequisiteFulfilled(fulfilledPrerequisite);
            }
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
        private final Task taskFrom;
        private final Task taskTo;
        private final int cost;
        protected CommunicationCost(Task taskFrom, Task taskTo, int cost) {
            this.taskFrom = taskFrom;
            this.taskTo = taskTo;
            this.cost = cost;
        }

        public int getCost() {
            return this.cost;
        }

        public Task getChildTask() {
            return this.taskTo;
        }
    }

}
