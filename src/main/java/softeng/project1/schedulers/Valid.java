package softeng.project1.schedulers;

import softeng.project1.graph.TaskGraph;
import softeng.project1.graph.processors.Processors;

import java.util.ArrayList;
import java.util.List;

public class Valid {
    private TaskGraph graph;
//    private List<Processors> processors = new ArrayList<Processors>();

    public Valid() {

    }
    public Valid(TaskGraph original) {
        graph = original;
    }

    private class Task {
        private int weight;
        private int start;
        private int processor;
        private String name;
        protected Task(int weight, String name) {
            this.weight = weight;
            this.name = name;
        }

        protected void assignStart (int start) {
            this.start = start;
        }

        protected void assignProcessor (int processor) {
            this.processor = processor;
        }
    }

    private class Switch {
        private Task task1;
        private Task task2;
        private int switchWeight; // weight to go from task 1 to task 2
        protected Switch (Task task1, Task task2, int weight) {
            this.task1 = task1;
            this.task2 = task2;
            this.switchWeight = weight;
        }
    }
}
