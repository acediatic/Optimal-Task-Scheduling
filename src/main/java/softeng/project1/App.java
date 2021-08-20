package softeng.project1;

import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.algorithms.astar.heuristics.ParallelAStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.SequentialAStarHeuristicManager;
import softeng.project1.algorithms.astar.parallel.ParallelAStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.graph.Schedule;
import softeng.project1.io.AStarIOHandler;
import softeng.project1.io.CommandLineProcessor;
import softeng.project1.io.IOHandler;

import java.util.HashMap;
import java.util.PriorityQueue;


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
                String.format("-- Cores used in determining schedule: %d\n", clp.getNumThreads()) +
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

        Schedule originalSchedule = ioHandler.readFile();
        HeuristicManager heuristicManager;
        SchedulingAlgorithm algorithm;

        if (clp.getNumThreads() > 1) {
            // PARALLEL
            heuristicManager = new ParallelAStarHeuristicManager(ioHandler.getSumWeights(), clp.getNumProcessors(), ioHandler.getListSchedulingAlgoStep());
            algorithm = new ParallelAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    clp.getNumThreads(),
                    ioHandler.getListSchedulingAlgoStep()
            );
        } else {
            // SEQUENTIAL
            heuristicManager = new SequentialAStarHeuristicManager(ioHandler.getSumWeights(), clp.getNumProcessors(), ioHandler.getListSchedulingAlgoStep());
            algorithm = new SequentialAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    new HashMap<>(),
                    new PriorityQueue<>(),
                    ioHandler.getListSchedulingAlgoStep()
            );
        }


        String result = ioHandler.writeFile(algorithm.generateSchedule());
        System.out.println(result);

//        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(ListIOHandler.readFile(clp.getInputFileName()), clp.getNumProcessors());
//
//        List<int[]> schedule = listScheduler.generateSchedule();
//
//        ListIOHandler.writeFile(listScheduler.scheduleToGraph(schedule), clp.getGraphName(), clp.getOutputFileName());
//
        System.out.println("Successfully created " + clp.getOutputFileName() + '\n');
    }
}
