package softeng.project1;

import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.PriorityQueueHeuristicManager;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.algorithms.valid.ListSchedulingAlgorithm;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.processors.Processors;
import softeng.project1.io.AStarIOHandler;
import softeng.project1.io.CommandLineProcessor;
import softeng.project1.io.IOHandler;
import softeng.project1.io.ListIOHandler;

import java.util.HashMap;
import java.util.List;


public final class App {
    private App() {
    }

    /**
     * Runs the scheduling program
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        CommandLineProcessor clp = new CommandLineProcessor(args);

        String runInformation = "***** Outsourced to Pakistan - Scheduling Algorithm *****\n" +
                String.format("- Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("- Number of Processors Available for Tasks: %o\n", clp.getNumProcessors()) +
                String.format("- Storing output in file: %s\n\n", clp.getOutputFileName()) +
                String.format("-- Cores used in determining schedule: %o\n", clp.getNumThreads()) +
                "-- visualisation " + (clp.isVisual() ? "on" : "off") +
                "\n";

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.

        IOHandler ioHandler = new AStarIOHandler(
                clp.getInputFileName(),
                clp.getOutputFileName(),
                clp.getGraphName(),
                clp.getNumProcessors()
        );

        SchedulingAlgorithm schedulingAlgorithm = new SequentialAStarSchedulingAlgorithm(
                ioHandler.readFile(),
                new PriorityQueueHeuristicManager(), // TODO... find what this needs
                new HashMap<>()
        );

        ioHandler.writeFile(schedulingAlgorithm.generateSchedule());

//        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(ListIOHandler.readFile(clp.getInputFileName()), clp.getNumProcessors());
//
//        List<int[]> schedule = listScheduler.generateSchedule();
//
//        ListIOHandler.writeFile(listScheduler.scheduleToGraph(schedule), clp.getGraphName(), clp.getOutputFileName());
//
        System.out.println("Successfully created " + clp.getOutputFileName() + '\n');
    }
}
