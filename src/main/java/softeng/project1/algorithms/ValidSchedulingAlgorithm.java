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
    private final List<CommunicationCost> communicationCosts;


//    private List<Processors> processors = new ArrayList<Processors>();

    public ValidSchedulingAlgorithm(Graph read) {
        this(read, DEFAULT_NUMBER_OF_PROCESSORS);
    }

    public ValidSchedulingAlgorithm(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.numberOfProcessors = numberOfProcessors;
        this.tasksInTopology = null // TODO... get from graph
        this.processors = new ArrayList<>(numberOfProcessors);
        this.communicationCosts = new ArrayList<>();

        for (int i = 0; i<this.numberOfProcessors; i++) {
            processors.add(new ValidProcessor(i));
        }
    }

    public void graphToTaskAndCC() {

    }

    public void scheduleToGraph() {

    }

    private int getCostForProcessor(Task task, ValidProcessor processor) {
        int cost = 0;
        int checkCost;
        List<Task> prereqs = task.getPrerequisites();
        for (Task prereq: prereqs) {
            for (CommunicationCost communicationCost: communicationCosts) {
                checkCost = communicationCost.tellCommunicationCost(prereq, task);
                if (checkCost != -1) {
                    if (!processor.checkTaskIn(prereq)) {
                        cost = cost + checkCost;
                    }
                }
            }
        }
        return cost;
    }

    private void compareAndAdd (Task task) {
        int minId = 0;
        int lowestTime = processors.get(minId).getOngoingTime();
        for (ValidProcessor processor: processors) {
            int time = processor.getOngoingTime() + getCostForProcessor(task, processor);
            if (time < lowestTime) {
                lowestTime = time;
                minId = processor.getId();
            }
        }
        processors.get(minId).addTask(task);
    }

    public void schedule() {
        for (Task task: tasksInTopology) {
            compareAndAdd(task);
        }
    }

    private class ValidProcessor {
        private final int id;
        private final List<Task> tasks = new ArrayList<Task>();
        private int ongoingTime = 0;

        protected ValidProcessor(int id) {
            this.id = id;
        }

        protected void addTask(Task task) {
            task.assignStart(ongoingTime);
            tasks.add(task);
            this.ongoingTime += task.getWeight();
        }

        protected int getOngoingTime() {
            return this.ongoingTime;
        }

        protected int getId() {
            return this.id;
        }

        protected boolean checkTaskIn(Task task) {
            return this.tasks.contains(task);
        }
    }
    private class Task {
        private List<Task> prerequisites;
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

        protected List<Task> getPrerequisites() {
            return this.prerequisites;
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
