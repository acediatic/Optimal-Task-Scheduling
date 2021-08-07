package softeng.project1.algorithms;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.processors.processor.Processor;

import java.util.ArrayList;
import java.util.List;

public class Valid implements SchedulingAlgorithm {
    private Graph graph; // Graph to process
    private List<Task> tasksInTopology; // Need to be implemented from graph data
    private List<ValidProcessor> processors = new ArrayList<ValidProcessor>();
    private List<CommunicationCost> communicationCosts = new ArrayList<CommunicationCost>();
    private static int defaultNumberOfProcessors = 1;  //default number of processors to begin with
    private int numberOfProcessors;
//    private List<Processors> processors = new ArrayList<Processors>();

    public Valid(Graph read) {
        this(read, defaultNumberOfProcessors);
    }

    public Valid(Graph read, int numberOfProcessors) {
        this.graph = read;
        this.numberOfProcessors = numberOfProcessors;
        for (int i = 0; i<this.numberOfProcessors; i++) {
            ValidProcessor processor = new ValidProcessor(i);
            processors.add(processor);
        }
    }

    public void graphToTaskAndCC() {

    }

    public void scheduleToGraph() {

    }

    private int getCostForProcessor(Task task, ValidProcessor processor) {
        int cost = 0;
        List<Task> prereqs = task.getPrerequisites();
        for (Task prereq: prereqs) {
            for (CommunicationCost communicationCost: communicationCosts) {
                int checkCost = communicationCost.tellCommunicationCost(prereq, task);
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
        private int id;
        private List<Task> tasks = new ArrayList<Task>();
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
            if (this.tasks.contains(task)) {
                return true;
            } else {
                return false;
            }
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
